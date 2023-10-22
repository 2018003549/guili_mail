package com.liao.gulimal.gulimalcoupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.liao.gulimal.gulimalcoupon.dao")
@ComponentScan("com.liao.gulimal.gulimalcoupon.service")
@SpringBootApplication
public class GuliMalCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliMalCouponApplication.class, args);
    }

}
