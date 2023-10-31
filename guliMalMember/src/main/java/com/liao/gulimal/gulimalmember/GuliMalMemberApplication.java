package com.liao.gulimal.gulimalmember;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.liao.gulimal.gulimalmember.fegin")
public class GuliMalMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliMalMemberApplication.class, args);
    }

}
