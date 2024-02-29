package com.liao.gulimall.gulimallcart.service;

import com.liao.gulimall.gulimallcart.vo.Cart;
import com.liao.gulimall.gulimallcart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {
    CartItem addCart(Long skuId, int num) throws ExecutionException, InterruptedException;

    CartItem getCartItemBySkuId(String skuId);

    Cart getCart() throws ExecutionException, InterruptedException;

    void checkItem(Long skuId, Integer checked);

    void countItem(Long skuId, Integer num);

    void deleteItem(Long skuId);

    List<CartItem> getCurrentUserCartItems();
}
