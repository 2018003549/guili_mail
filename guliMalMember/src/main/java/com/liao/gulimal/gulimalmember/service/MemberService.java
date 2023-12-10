package com.liao.gulimal.gulimalmember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liao.common.utils.PageUtils;
import com.liao.gulimal.gulimalmember.entity.MemberEntity;
import com.liao.gulimal.gulimalmember.exception.PhoneException;
import com.liao.gulimal.gulimalmember.exception.UserNameExistException;
import com.liao.gulimal.gulimalmember.vo.MemberLoginVo;
import com.liao.gulimal.gulimalmember.vo.MemberRegistVo;
import com.liao.gulimal.gulimalmember.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:21:15
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo vo);
    void checkPhoneUnique(String phone)throws PhoneException;
    void checkUserNameUnique(String username)throws UserNameExistException;

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser socialUser) throws Exception;
}

