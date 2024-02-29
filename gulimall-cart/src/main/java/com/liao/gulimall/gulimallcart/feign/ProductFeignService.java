package com.liao.gulimall.gulimallcart.feign;

import com.liao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("gulimall-product")
public interface ProductFeignService {
    @RequestMapping("/gulimalProduct/skuinfo/info/{skuId}")
     R info(@PathVariable("skuId") Long skuId);
    @RequestMapping("/gulimalProduct/skusaleattrvalue/stringList/{skuId}")
    List<String> getStringListById(@PathVariable("skuId") Long skuId);
    @GetMapping("/gulimalProduct/skuinfo/{skuId}/price")
    public BigDecimal getPrice(@PathVariable("skuId")Long skuId);
}
