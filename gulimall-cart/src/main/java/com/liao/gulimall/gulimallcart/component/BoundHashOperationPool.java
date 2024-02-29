package com.liao.gulimall.gulimallcart.component;

import com.liao.constant.CartConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
@Component
public class BoundHashOperationPool {
    @Autowired
    StringRedisTemplate redisTemplate;
    String cartKey = CartConstant.CART_PREFIX;
    ConcurrentHashMap<String,BoundHashOperations<String, Object, Object>> boundUserCartMap=new ConcurrentHashMap<>();
    public BoundHashOperationPool(){

    }
    public BoundHashOperations<String, Object, Object> getBoundUserCart(String userId){
        if(boundUserCartMap.containsKey(userId)){
            return boundUserCartMap.get(userId);
        }else{
            synchronized (this){
                if(boundUserCartMap.containsKey(userId)){
                    //双检加锁，防止其它线程已经put过，自己又重复put了一遍
                    return boundUserCartMap.get(userId);
                }
                String userCartKey=this.cartKey+userId;
                BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(userCartKey);
                boundUserCartMap.put(userId,operations);//之后该用户的购物车绑定对象直接从池子中拿就行了，不需要重新绑定了
                return operations;
            }
        }
    }
}
