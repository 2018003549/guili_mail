package com.liao.gulimal.gulimallgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

@Configuration
public class MyCorsConfiguration {
    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();//与跨越有关的配置类
        //配置跨域
        corsConfiguration.addAllowedHeader("*");//允许哪些头进行跨域
        corsConfiguration.addAllowedMethod("*");//允许哪些请求方式进行跨域
        corsConfiguration.addAllowedOriginPattern("*");//允许哪些请求来源进行跨域
        corsConfiguration.setAllowCredentials(true);//是否允许携带cookie跨域
        //注册跨域配置
        source.registerCorsConfiguration("/**",corsConfiguration);//所有请求路径都适用
        return new CorsWebFilter(source);
    }
}
