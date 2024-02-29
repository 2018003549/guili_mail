package com.liao.gulimall.gulimallcart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Cart {
    List<CartItem> items;
    private Integer countNum;//商品数量
    private Integer countType;//商品类型数量
    private BigDecimal totalAmount;//商品总价
    private BigDecimal reduce=new BigDecimal(0);//减免价格
    //计算商品总量
    public Integer getCountNum() {
        int sum=0;
        if(items!=null&&items.size()>0){
            for (CartItem item : items) {
                sum+=item.getCount();
            }
        }
        return sum;
    }
    //计算包含的商品类型数量
    public Integer getCountType() {
        int count=0;
        if(items!=null&&items.size()>0){
            for (CartItem item : items) {
                count++;
            }
        }
        return countType;
    }
    //计算总价
    public BigDecimal getTotalAmount() {
        BigDecimal amount=new BigDecimal(0);
        //先计算购物项的总价
        if(items!=null&&items.size()>0){
            for (CartItem item : items) {
                if(item.getCheck()){
                    //只计算被选中商品的总价
                    amount=amount.add(item.getTotalPrice());
                }
            }
        }
        //然后减去减免价格
        amount=amount.subtract(getReduce());
        return amount;
    }
}
