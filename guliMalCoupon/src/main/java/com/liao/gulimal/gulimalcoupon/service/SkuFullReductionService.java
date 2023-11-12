package com.liao.gulimal.gulimalcoupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liao.common.to.MemberPrice;
import com.liao.common.to.SkuReductionTo;
import com.liao.common.utils.PageUtils;
import com.liao.gulimal.gulimalcoupon.entity.SkuFullReductionEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品满减信息
 *
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 19:53:15
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo reductionTo);
}

