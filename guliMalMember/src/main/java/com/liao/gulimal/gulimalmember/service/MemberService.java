package com.liao.gulimal.gulimalmember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liao.common.utils.PageUtils;
import com.liao.gulimal.gulimalmember.entity.MemberEntity;

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
}

