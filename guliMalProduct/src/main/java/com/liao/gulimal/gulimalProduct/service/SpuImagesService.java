package com.liao.gulimal.gulimalProduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liao.common.utils.PageUtils;
import com.liao.gulimal.gulimalProduct.entity.SpuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:11:14
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveImages(Long id, List<String> images);
}

