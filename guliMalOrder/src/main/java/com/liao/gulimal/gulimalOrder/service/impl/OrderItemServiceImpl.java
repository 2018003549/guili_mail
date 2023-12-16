package com.liao.gulimal.gulimalOrder.service.impl;

import com.liao.gulimal.gulimalOrder.entity.OrderEntity;
import com.liao.gulimal.gulimalOrder.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.Query;

import com.liao.gulimal.gulimalOrder.dao.OrderItemDao;
import com.liao.gulimal.gulimalOrder.entity.OrderItemEntity;
import com.liao.gulimal.gulimalOrder.service.OrderItemService;

@RabbitListener(queues = {"hello-java-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    @RabbitHandler
    public void recieveMessage(OrderReturnReasonEntity content, Message message,Channel channel) throws IOException {
        System.out.println("监听到了OrderReturnReasonEntity类型的消息" + content);
        //需要传递当前消息派发的标识【Message中有封装，该标识是在当前通道内自增的】和是否批量签收
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
    @RabbitHandler
    public void recieveMessage2(OrderEntity content, Message message,Channel channel) throws IOException{
        System.out.println("监听到了OrderReturnReasonEntity类型的消息" + content);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        if(deliveryTag%2==0){
            System.out.println("拒签==》"+deliveryTag);
            channel.basicNack(deliveryTag, false,true);
        }else {
            System.out.println("已经签收==》"+deliveryTag);
            channel.basicAck(deliveryTag,false);
        }
    }
}