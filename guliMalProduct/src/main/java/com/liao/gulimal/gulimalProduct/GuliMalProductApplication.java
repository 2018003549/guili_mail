package com.liao.gulimal.gulimalProduct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.liao.gulimal.gulimalProduct.fegin")
@EnableDiscoveryClient
@SpringBootApplication
public class GuliMalProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuliMalProductApplication.class, args);
    }
}
