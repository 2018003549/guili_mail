package com.liao.gulimal.gulimalOrder.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.liao.common.to.MemberRespVo;
import com.liao.common.to.mq.OrderTo;
import com.liao.common.utils.R;
import com.liao.constant.OrderConstant;
import com.liao.exception.NoStockException;
import com.liao.gulimal.gulimalOrder.dao.OrderItemDao;
import com.liao.gulimal.gulimalOrder.entity.OrderItemEntity;
import com.liao.gulimal.gulimalOrder.enume.OrderStatusEnum;
import com.liao.gulimal.gulimalOrder.feign.CartFeignService;
import com.liao.gulimal.gulimalOrder.feign.MemberFeignService;
import com.liao.gulimal.gulimalOrder.feign.ProductFeignService;
import com.liao.gulimal.gulimalOrder.feign.WmsFeignService;
import com.liao.gulimal.gulimalOrder.interceptor.LoginUserInterceptor;
import com.liao.gulimal.gulimalOrder.service.OrderItemService;
import com.liao.gulimal.gulimalOrder.to.OrderCreateTo;
import com.liao.gulimal.gulimalOrder.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.Query;

import com.liao.gulimal.gulimalOrder.dao.OrderDao;
import com.liao.gulimal.gulimalOrder.entity.OrderEntity;
import com.liao.gulimal.gulimalOrder.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    WmsFeignService wmsFeignService;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();//获取老请求的信息
        //1.远程查询当前用户的所有收获地址列表
        CompletableFuture<Void> getAddresses = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(attributes);//给新的异步线程共享老请求信息
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);
        });
        CompletableFuture<Void> getCartItems = CompletableFuture.runAsync(() -> {
            //2.远程查询购物车所有选中的购物项
            RequestContextHolder.setRequestAttributes(attributes);
            List<OrderItemVo> cartItems = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(cartItems);
        }).thenRunAsync(()->{
            RequestContextHolder.setRequestAttributes(attributes);
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> skuIds = items.stream().map(item -> {
                return item.getSkuId();
            }).collect(Collectors.toList());
            R r = wmsFeignService.getSkusHasStock(skuIds);
            List<SkuHasStockVo> data = r.getData(new TypeReference<List<SkuHasStockVo>>() {
            });
            if(data!=null){
                Map<Long, Boolean> map = data.stream().//存储每个购物项的库存情况
                        collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
                confirmVo.setStocks(map);
            }
        },executor);
        //3.查询用户积分
        Integer integration = memberRespVo.getIntegration();//当前登录的用户信息就有积分信息，直接获取
        confirmVo.setIntegration(integration);
        CompletableFuture.allOf(getAddresses,getCartItems).get();
        //4.创建防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberRespVo.getId(),token,
                30, TimeUnit.MINUTES);//服务器存储防重令牌
        confirmVo.setOrderToken(token);//页面携带防重令牌
        return confirmVo;
    }
//    @GlobalTransactional高并发功能不适合使用seata的分布式事务
    @Transactional
    @Override
    public SumbitOrderResponseVo sumbitOrder(OrderSubmitVo orderSubmitVo) {
        confirmVoThreadLocal.set(orderSubmitVo);//共享该vo对象，其它方法就不用传参数了
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();//从拦截器获取用户信息
        SumbitOrderResponseVo responseVo = new SumbitOrderResponseVo();//封装返回数据
        //创建订单、验证令牌、验价格、锁库存
        //1.验证令牌【保证幂等性】
        String script="if redis.call('get', KEYS[1]) == ARGV[1] " +
                "then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = orderSubmitVo.getOrderToken();
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),//lua脚本
                Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()),//使用到的key列表
                orderToken);//可变参数列表
        if(result==0){
            //lua脚本返回值是0【校验失败】和1【成功】
            responseVo.setCode(1);
            return responseVo;
        }else {
            //2.验证成功就创建订单
            OrderCreateTo order = createOrder();
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = orderSubmitVo.getPayPrice();
            //3.验价，创建订单的时候会给实体赋最新的价格信息，和前端传输的金额进行对比
            if(Math.abs(payAmount.subtract(payPrice).doubleValue())<0.01){
                //4.保存订单
                saveOrder(order);
                WareSkuLockVo lockVo=new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> collect = order.getOrderItems().stream().map((item) -> {
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setSkuId(item.getSkuId());
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setTitle(item.getSkuName());
                    itemVo.setPrice(item.getSkuPrice());
                    return itemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(collect);
                R r = wmsFeignService.orderLockStock(lockVo);
                if(r.getCode()==0){
                    //锁定成功了
                    responseVo.setOrder(order.getOrder());
                    responseVo.setCode(0);
                    //订单创建成功需要发送消息到延时队列中
                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order.getOrder());
                    return responseVo;
                }else {
                    throw new NoStockException(1l);
                }
            }else {
                responseVo.setCode(2);
                return responseVo;
            }
        }
    }

    @Override
    public Integer getOrderByOrderSn(String orderSn) {
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        if(orderEntity!=null){
            return orderEntity.getStatus();
        }
        return null;
    }

    @Override
    public void closeOrder(OrderEntity entity) {
        //先查询当前订单的最新状态
        OrderEntity orderEntity = this.getById(entity.getId());
        if(orderEntity!=null&&orderEntity.getStatus()
        ==OrderStatusEnum.CREATE_NEW.getCode()){
            //只有超时且尚未付款的订单需要关单
            orderEntity.setStatus(OrderStatusEnum.CANCLED.getCode());//更改订单状态为已取消
            this.updateById(orderEntity);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity,orderTo);
            //关闭订单立即向解锁队列发消息
            try{
                //保证消息一定会发送出去，每个消息都要做好日志记录
                //定期扫描数据库，检查消息状态
                rabbitTemplate.convertAndSend("order-event-exchange","stock.release.order",orderTo);
            }catch (Exception e){
                //将没发送成功的消息重发
            }

        }
    }

    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        this.save(orderEntity);//保存订单信息
        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);//批量保存订单项信息
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        //总价
        BigDecimal total = new BigDecimal("0.0");
        //优惠价
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal intergration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        //积分、成长值
        Integer integrationTotal = 0;
        Integer growthTotal = 0;
        //订单总额，叠加每一个订单项的总额信息
        for (OrderItemEntity orderItem : orderItemEntities) {
            //优惠价格信息
            coupon = coupon.add(orderItem.getCouponAmount());
            promotion = promotion.add(orderItem.getPromotionAmount());
            intergration = intergration.add(orderItem.getIntegrationAmount());
            //总价
            total = total.add(orderItem.getRealAmount());
            //积分信息和成长值信息
            integrationTotal += orderItem.getGiftIntegration();
            growthTotal += orderItem.getGiftGrowth();

        }
        //1、订单价格相关的
        orderEntity.setTotalAmount(total);
        //设置应付总额(总额+运费)
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(intergration);
        //设置积分成长值信息
        orderEntity.setIntegration(integrationTotal);
        orderEntity.setGrowth(growthTotal);
        //设置删除状态(0-未删除，1-已删除)
        orderEntity.setDeleteStatus(0);
    }
    private OrderCreateTo createOrder() {
        OrderCreateTo createTo = new OrderCreateTo();
        //1、生成订单号
        String orderSn = IdWorker.getTimeId().substring(0,32);
        OrderEntity orderEntity = builderOrder(orderSn);//屎山代码！！！！
        //2、获取到所有的订单项
        List<OrderItemEntity> orderItemEntities = builderOrderItems(orderSn);
        //3、更新价格、积分等信息
        computePrice(orderEntity,orderItemEntities);
        createTo.setOrder(orderEntity);
        createTo.setOrderItems(orderItemEntities);
        return createTo;
    }

    private List<OrderItemEntity> builderOrderItems(String orderSn) {
        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        //最后确定每个购物项的价格
        List<OrderItemVo> currentCartItems = cartFeignService.getCurrentUserCartItems();
        if (currentCartItems != null && currentCartItems.size() > 0) {
            orderItemEntityList = currentCartItems.stream().map((items) -> {
                //构建订单项数据
                OrderItemEntity orderItemEntity = builderOrderItem(items);
                orderItemEntity.setOrderSn(orderSn);
                return orderItemEntity;
            }).collect(Collectors.toList());
        }
        return orderItemEntityList;
    }

    private OrderItemEntity builderOrderItem(OrderItemVo items) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        //1、商品的spu信息
        Long skuId = items.getSkuId();
        //远程获取spu的信息
        R spuInfo = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo spuInfoData = spuInfo.getData("data", new TypeReference<SpuInfoVo>() {
        });
        orderItemEntity.setSpuId(spuInfoData.getId());
        orderItemEntity.setSpuName(spuInfoData.getSpuName());
        orderItemEntity.setSpuBrand(spuInfoData.getBrandId().toString());
        orderItemEntity.setCategoryId(spuInfoData.getCatalogId());

        //2、商品的sku信息
        orderItemEntity.setSkuId(skuId);
        orderItemEntity.setSkuName(items.getTitle());
        orderItemEntity.setSkuPic(items.getImage());
        orderItemEntity.setSkuPrice(items.getPrice());
        orderItemEntity.setSkuQuantity(items.getCount());

        //将list集合转换为String，并指定每个元素之间的分隔符
        String skuAttrValues = StringUtils.collectionToDelimitedString(items.getSkuAttr(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttrValues);
        //3、商品的优惠信息【不做】
        //4、商品的积分信息
        orderItemEntity.setGiftGrowth(items.getPrice().multiply(new BigDecimal(items.getCount())).intValue());
        orderItemEntity.setGiftIntegration(items.getPrice().multiply(new BigDecimal(items.getCount())).intValue());
        //5、订单项的价格信息
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);
        //当前订单项的实际金额.总额 - 各种优惠价格
        //原来的价格
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        //原价减去优惠价得到最终的价格
        BigDecimal subtract = origin.subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(subtract);
        return orderItemEntity;
    }

    /**
     * 创建订单实体的屎山代码，运费和地址信息直接从前端获取得了
     */
    private OrderEntity builderOrder(String orderSn) {
        //获取当前用户登录信息
        MemberRespVo memberResponseVo = LoginUserInterceptor.loginUser.get();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberId(memberResponseVo.getId());
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberUsername(memberResponseVo.getUsername());
        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();
        //远程获取收货地址和运费信息
        R fareAddressVo = wmsFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareResp = fareAddressVo.getData("data", new TypeReference<FareVo>() {});
        //获取到运费信息
        BigDecimal fare = fareResp.getFare();
        orderEntity.setFreightAmount(fare);
        //获取到收货地址信息
        MemberAddressVo address = fareResp.getAddress();
        //设置收货人信息
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverRegion(address.getRegion());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());
        //设置订单相关的状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setConfirmStatus(0);
        return orderEntity;
    }
}