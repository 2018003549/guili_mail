package com.liao.gulimal.gulimalcoupon.dao;

import com.liao.gulimal.gulimalcoupon.entity.CouponSpuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 * 
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-21 19:54:37
 */
@Mapper
public interface CouponSpuRelationDao extends BaseMapper<CouponSpuRelationEntity> {
	
}
