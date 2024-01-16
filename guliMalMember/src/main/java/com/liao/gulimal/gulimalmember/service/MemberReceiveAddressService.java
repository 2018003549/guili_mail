package com.liao.gulimal.gulimalmember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liao.common.utils.PageUtils;
import com.liao.gulimal.gulimalmember.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:21:15
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<MemberReceiveAddressEntity> getAddresses(Long memberId);
}

