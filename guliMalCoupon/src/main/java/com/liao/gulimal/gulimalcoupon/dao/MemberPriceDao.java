package com.liao.gulimal.gulimalcoupon.dao;

import com.liao.gulimal.gulimalcoupon.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 19:53:15
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
