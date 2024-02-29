package com.liao.gulimall.gulimallcart.to;

import lombok.Data;

@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;
    private boolean isLogin=false;
}
