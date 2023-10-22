package com.liao.gulimal.gulimalcoupon;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liao.gulimal.gulimalcoupon.entity.MemberPriceEntity;
import com.liao.gulimal.gulimalcoupon.service.MemberPriceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GuliMalCouponApplicationTests {
    @Autowired
    MemberPriceService memberPriceService;
    @Test
    void contextLoads() {
        List<MemberPriceEntity> list = memberPriceService.list(new QueryWrapper<MemberPriceEntity>().eq("id", 1l));
        list.forEach((item)->{
            System.out.println(item);
        });
    }

}
