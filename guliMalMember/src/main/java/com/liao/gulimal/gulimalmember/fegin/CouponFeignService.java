package com.liao.gulimal.gulimalmember.fegin;

import com.liao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@FeignClient("gulimal-coupon")
public interface CouponFeignService {
    @RequestMapping(value = "/gulimalcoupon/coupon/member/list")
    public R memberCoupons();
}
