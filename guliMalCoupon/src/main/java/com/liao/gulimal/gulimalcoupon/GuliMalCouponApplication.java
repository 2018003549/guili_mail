package com.liao.gulimal.gulimalcoupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.liao.gulimal.gulimalcoupon.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class GuliMalCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliMalCouponApplication.class, args);
    }

}
