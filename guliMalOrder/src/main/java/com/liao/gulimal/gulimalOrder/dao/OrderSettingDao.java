package com.liao.gulimal.gulimalOrder.dao;

import com.liao.gulimal.gulimalOrder.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:32:24
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
