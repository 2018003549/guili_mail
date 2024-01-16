package com.liao.gulimal.gulimalOrder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liao.common.utils.PageUtils;
import com.liao.gulimal.gulimalOrder.entity.OrderEntity;
import com.liao.gulimal.gulimalOrder.vo.OrderConfirmVo;
import com.liao.gulimal.gulimalOrder.vo.OrderSubmitVo;
import com.liao.gulimal.gulimalOrder.vo.SumbitOrderResponseVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:32:24
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SumbitOrderResponseVo sumbitOrder(OrderSubmitVo orderSubmitVo);

    Integer getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity entity);

}

