package com.liao.gulimall.gulimallcart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.liao.common.utils.R;
import com.liao.constant.CartConstant;
import com.liao.gulimall.gulimallcart.component.BoundHashOperationPool;
import com.liao.gulimall.gulimallcart.feign.ProductFeignService;
import com.liao.gulimall.gulimallcart.interceptor.CartInterceptor;
import com.liao.gulimall.gulimallcart.service.CartService;
import com.liao.gulimall.gulimallcart.to.UserInfoTo;
import com.liao.gulimall.gulimallcart.vo.Cart;
import com.liao.gulimall.gulimallcart.vo.CartItem;
import com.liao.gulimall.gulimallcart.vo.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    BoundHashOperationPool boundHashOperationPool;
    @Override
    public CartItem addCart(Long skuId, int num) throws ExecutionException, InterruptedException {
        //1.获取购物车的操作对象
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(res)) {
            //购物车无此商品
            CartItem cartItem = new CartItem();
            //2.远程查询要添加的商品基本信息
            CompletableFuture<Void> getBaseInfo = CompletableFuture.runAsync(() -> {
                R skuInfo = productFeignService.info(skuId);
                SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                //2.1.同步购物项信息
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(data.getSkuDefaultImg());
                cartItem.setTitle(data.getSkuTitle());
                cartItem.setSkuId(skuId);
                cartItem.setPrice(data.getPrice());
            }, executor);
            //3.远程查询sku的销售属性
            CompletableFuture<Void> getSaleAttrs = CompletableFuture.runAsync(() -> {
                List<String> saleAttrs = productFeignService.getStringListById(skuId);
                cartItem.setSkuAttr(saleAttrs);
            }, executor);
            //4.等待所有异步任务查询完成就存取数据
            CompletableFuture.allOf(getBaseInfo, getSaleAttrs).get();
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        } else {
            //购物车有当前商品，就修改数量即可
            CartItem item = JSON.parseObject(res, CartItem.class);
            item.setCount(item.getCount() + num);
            cartOps.put(skuId.toString(), JSON.toJSONString(item));
            return item;
        }
    }

    @Override
    public CartItem getCartItemBySkuId(String skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res = (String) cartOps.get(skuId);
        return JSON.parseObject(res, CartItem.class);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Cart cart = new Cart();
        if (userInfoTo.getUserId() != null) {
            String userId = Long.toString(userInfoTo.getUserId());
            String userKey= userInfoTo.getUserKey();
            //1.先检查临时购物车的数据是否已经合并
            List<CartItem> tempCartItems = getCartItems(userKey);
            //2.合并购物车
            if(tempCartItems!=null&&tempCartItems.size()>0){
                //2.1临时购物车还有数据则需要合并
                for (CartItem tempCartItem : tempCartItems) {
                    //因为当前有用户登录，添加购物项的操作是添加给登录用户的
                    addCart(tempCartItem.getSkuId(),tempCartItem.getCount());
                }
                //2.2合并完后需要删除临时购物车的键
                redisTemplate.delete(CartConstant.CART_PREFIX+userKey);
            }
            //3.获取登陆后的购物车的数据【包含合并的临时购物车的数据】
            List<CartItem> cartItems = getCartItems(userId);
            if(cartItems!=null&&cartItems.size()>0){
                cart.setItems(cartItems);
            }
        } else {
            //没登陆就获取临时购物车的所有购物项
            List<CartItem> cartItems = getCartItems( userInfoTo.getUserKey());
            if(cartItems!=null&&cartItems.size()>0){
                cart.setItems(cartItems);
            }
        }
        return cart;
    }

    @Override
    public void checkItem(Long skuId, Integer checked) {
        CartItem cartItemBySkuId = getCartItemBySkuId(skuId.toString());
        cartItemBySkuId.setCheck(checked==1);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),JSON.toJSONString(cartItemBySkuId));
    }

    @Override
    public void countItem(Long skuId, Integer num) {
        CartItem cartItemBySkuId = getCartItemBySkuId(skuId.toString());
        cartItemBySkuId.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),JSON.toJSONString(cartItemBySkuId));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getCurrentUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId()==null){
            return null;
        }else {
            List<CartItem> cartItems = getCartItems(Long.toString(userInfoTo.getUserId()));
            List<CartItem> collect = cartItems.stream().//过滤所有被选中的购物项
                    filter(cartItem -> cartItem.getCheck()).
                    map(cartItem->{
                        cartItem.setPrice(productFeignService.getPrice(cartItem.getSkuId()));//更新最新的价格
                        return cartItem;
                    }).
                    collect(Collectors.toList());
            return collect;
        }
    }


    public List<CartItem> getCartItems(String key) {
        BoundHashOperations<String, Object, Object> operations = boundHashOperationPool.getBoundUserCart(key);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            return values.stream().map((item) -> {
                return JSON.parseObject((String) item, CartItem.class);
            }).collect(Collectors.toList());
        }
        return null;
    }

    private BoundHashOperations<String, Object, Object> getCartOps() {
        //尝试使用享元模式
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            return boundHashOperationPool.getBoundUserCart(userInfoTo.getUserId().toString());
        } else {
            return boundHashOperationPool.getBoundUserCart(userInfoTo.getUserKey());
        }
    }
}
