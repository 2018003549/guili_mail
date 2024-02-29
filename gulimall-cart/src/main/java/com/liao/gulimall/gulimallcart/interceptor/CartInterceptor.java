package com.liao.gulimall.gulimallcart.interceptor;

import com.liao.common.to.MemberRespVo;
import com.liao.constant.AuthServerConstant;
import com.liao.constant.CartConstant;
import com.liao.gulimall.gulimallcart.to.UserInfoTo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Component
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTo> threadLocal=new ThreadLocal();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(memberRespVo!=null){
            //用户登陆了
            userInfoTo.setUserId(memberRespVo.getId());
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null && cookies.length>0){
            for (Cookie cookie : cookies) {
                String name=cookie.getName();
                if(CartConstant.TEMP_USER_COOKIE_NAME.equals(name)){
                    userInfoTo.setUserKey(cookie.getValue());
                    break;
                }
            }
        }
        if(userInfoTo.getUserKey()==null){
            //没有临时用户就必须创建一个
            userInfoTo.setUserKey( UUID.randomUUID().toString());
        }
        threadLocal.set(userInfoTo);//存储到threadLocal，供同一个请求共享
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        if(!userInfoTo.isLogin()){
            //如果用户未登录，就得把临时用户存入cookie
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("gulimall.com");//整个项目都携带
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
