package com.liao.gulimall.gulimallcart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartItem {
    private Long skuId;
    private Boolean check=true;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;//单价
    private Integer count;//总数
    private BigDecimal totalPrice;//总价
    public BigDecimal getTotalPrice() {
        //计算总价
        return this.price.multiply(new BigDecimal(this.count));
    }
}
