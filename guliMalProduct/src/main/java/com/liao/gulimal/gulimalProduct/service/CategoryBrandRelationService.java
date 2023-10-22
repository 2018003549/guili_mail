package com.liao.gulimal.gulimalProduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liao.common.utils.PageUtils;
import com.liao.gulimal.gulimalProduct.entity.CategoryBrandRelationEntity;

import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:11:14
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}
