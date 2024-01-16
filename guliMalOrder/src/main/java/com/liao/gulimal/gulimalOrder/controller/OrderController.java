package com.liao.gulimal.gulimalOrder.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.liao.gulimal.gulimalOrder.entity.OrderEntity;
import com.liao.gulimal.gulimalOrder.service.OrderService;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.R;



/**
 * 订单
 *
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:32:24
 */
@RestController
@RequestMapping("gulimalOrder/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/status/{orderSn}")
    public R getOrderStatus(@PathVariable("orderSn")String orderSn){
        Integer status=orderService.getOrderByOrderSn(orderSn);
        return R.ok().put("data",status);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
