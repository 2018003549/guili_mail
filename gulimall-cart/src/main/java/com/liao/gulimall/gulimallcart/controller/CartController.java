package com.liao.gulimall.gulimallcart.controller;

import com.liao.constant.AuthServerConstant;
import com.liao.gulimall.gulimallcart.interceptor.CartInterceptor;
import com.liao.gulimall.gulimallcart.service.CartService;
import com.liao.gulimall.gulimallcart.to.UserInfoTo;
import com.liao.gulimall.gulimallcart.vo.Cart;
import com.liao.gulimall.gulimallcart.vo.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
public class CartController {
    @Autowired
    CartService cartService;
    @ResponseBody
    @GetMapping("/currentUserCartItems")
    public List<CartItem> getCurrentUserCartItems(){
        return cartService.getCurrentUserCartItems();
    }
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        //获取同一个线程【同一次请求】的共享数据
        Cart cart=cartService.getCart();
        model.addAttribute("cart",cart);
        return "cartList";
    }
    @GetMapping("addCartItem")
    public String addCartItem(@RequestParam("skuId")Long skuId, @RequestParam("num")int num
    , RedirectAttributes attributes) throws ExecutionException, InterruptedException {
        cartService.addCart(skuId,num);
        attributes.addAttribute("skuId",skuId);//会携带在请求参数
        return "redirect:http://cart.gulimall.com/addCartItem.html";
    }
    @GetMapping("addCartItem.html")
    public String addCartItemToSuccess(Model model,@RequestParam("skuId")String skuId) {
        CartItem cartItem=cartService.getCartItemBySkuId(skuId);//获取购物车中某个购物项的信息
        model.addAttribute("cartItem",cartItem);
        return "success";
    }
    @GetMapping(value = "/checkItem")
    public String checkItem(@RequestParam(value = "skuId") Long skuId,
                            @RequestParam(value = "checked") Integer checked) {
        cartService.checkItem(skuId,checked);
        return "redirect:http://cart.gulimall.com/cart.html";
    }
    @GetMapping("/countItem")
    public String countItem(@RequestParam(value = "skuId") Long skuId,
                            @RequestParam(value = "num")Integer num){
        cartService.countItem(skuId,num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam(value = "skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }
}
