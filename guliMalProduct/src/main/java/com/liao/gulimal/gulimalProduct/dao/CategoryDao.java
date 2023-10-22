package com.liao.gulimal.gulimalProduct.dao;

import com.liao.gulimal.gulimalProduct.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:11:14
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
