package com.liao.gulimal.gulimalProduct.dao;

import com.liao.gulimal.gulimalProduct.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liao.gulimal.gulimalProduct.vo.itemSaleAttrsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:11:14
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<itemSaleAttrsVo> getSaleAttrsBySpuId(@Param("spuId") Long spuId);

    List<String> getStringListById(@Param("skuId") Long skuId);
}
