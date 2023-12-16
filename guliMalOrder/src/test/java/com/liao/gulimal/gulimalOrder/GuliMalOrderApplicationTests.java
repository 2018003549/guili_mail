package com.liao.gulimal.gulimalOrder;

import com.liao.gulimal.gulimalOrder.entity.OrderEntity;
import com.liao.gulimal.gulimalOrder.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

@Slf4j
@SpringBootTest
class GuliMalOrderApplicationTests {
    @Autowired
    AmqpAdmin amqpAdmin;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Test
    void sendMessage() {
        OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
        reasonEntity.setCreateTime(new Date());
        reasonEntity.setName("sdad");
        OrderEntity orderEntity=new OrderEntity();
        orderEntity.setCreateTime(new Date());
        for (long i = 0; i < 10; i++) {
            //模拟队列中有多条消息
            if(i%2==0){
                reasonEntity.setId(i);
                rabbitTemplate.convertAndSend("hello-java-exchange","hello1.java",
                        reasonEntity,new CorrelationData(UUID.randomUUID().toString()));
            }else {
                orderEntity.setId(i);
                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",
                        orderEntity,new CorrelationData(UUID.randomUUID().toString()));
            }
        }
    }
    @Test
    void createExchange(){
        DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
    }
    @Test
    void createQueue(){
        Queue queue = new Queue("hello-java-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
    }
    @Test
    void createBinding(){
        Binding binding = new Binding("hello-java-queue",Binding.DestinationType.QUEUE,
                "hello-java-exchange","hello.java",null);
        amqpAdmin.declareBinding(binding);
    }
}
