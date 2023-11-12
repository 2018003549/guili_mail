package com.liao.gulimal.gulimalProduct.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.Query;

import com.liao.gulimal.gulimalProduct.dao.ProductAttrValueDao;
import com.liao.gulimal.gulimalProduct.entity.ProductAttrValueEntity;
import com.liao.gulimal.gulimalProduct.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveProductAttr(List<ProductAttrValueEntity> attrValueEntities) {
        this.saveBatch(attrValueEntities);
    }

    @Override
    public List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId) {
        List<ProductAttrValueEntity> productAttrValueEntities = this.baseMapper.selectList(
                new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return productAttrValueEntities;
    }
    @Transactional
    @Override
    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities) {
        //1.删除spuId之前对应的所有属性【辞旧迎新】
        this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>()
                .eq("spu_id",spuId));
        //2.插入新的规格参数
        List<ProductAttrValueEntity> collect = entities.stream().map(item -> {
            item.setSpuId(spuId);
            return item;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

}