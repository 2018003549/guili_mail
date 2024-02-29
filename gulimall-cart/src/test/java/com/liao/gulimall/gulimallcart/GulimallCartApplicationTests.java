package com.liao.gulimall.gulimallcart;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class GulimallCartApplicationTests {

    @Test
    void contextLoads() {
        BigDecimal bigDecimal = new BigDecimal(0);
        bigDecimal.add(new BigDecimal(1));
        System.out.println(bigDecimal);
    }

}
