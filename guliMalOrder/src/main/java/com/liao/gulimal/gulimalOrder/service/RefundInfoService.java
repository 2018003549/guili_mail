package com.liao.gulimal.gulimalOrder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liao.common.utils.PageUtils;
import com.liao.gulimal.gulimalOrder.entity.RefundInfoEntity;

import java.util.Map;

/**
 * 退款信息
 *
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:32:24
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

