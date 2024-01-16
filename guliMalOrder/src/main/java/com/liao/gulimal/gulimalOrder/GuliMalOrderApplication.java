package com.liao.gulimal.gulimalOrder;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
@EnableFeignClients
@EnableRedisHttpSession
@EnableRabbit
@EnableDiscoveryClient
@SpringBootApplication
public class GuliMalOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuliMalOrderApplication.class, args);
    }
}
