package com.liao.gulimal.gulimalProduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liao.common.utils.PageUtils;
import com.liao.gulimal.gulimalProduct.entity.SpuInfoDescEntity;
import com.liao.gulimal.gulimalProduct.entity.SpuInfoEntity;
import com.liao.gulimal.gulimalProduct.vo.SPUSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:11:14
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SPUSaveVo spuInfo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void up(Long spuId);

    SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

