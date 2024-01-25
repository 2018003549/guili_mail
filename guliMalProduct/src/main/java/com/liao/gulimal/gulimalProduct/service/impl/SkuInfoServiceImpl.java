package com.liao.gulimal.gulimalProduct.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.liao.common.utils.R;
import com.liao.gulimal.gulimalProduct.entity.SkuImagesEntity;
import com.liao.gulimal.gulimalProduct.entity.SpuInfoDescEntity;
import com.liao.gulimal.gulimalProduct.fegin.SeckillFeginService;
import com.liao.gulimal.gulimalProduct.service.*;
import com.liao.gulimal.gulimalProduct.vo.SeckillSkuRelationVo;
import com.liao.gulimal.gulimalProduct.vo.SkuItemVo;
import com.liao.gulimal.gulimalProduct.vo.SpuItemAttrGroupVo;
import com.liao.gulimal.gulimalProduct.vo.itemSaleAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.Query;

import com.liao.gulimal.gulimalProduct.dao.SkuInfoDao;
import com.liao.gulimal.gulimalProduct.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    SkuImagesService imagesService;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    AttrGroupService attrGroupService;
    @Autowired
    SkuSaleAttrValueService saleAttrValueService;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    SeckillFeginService seckillFeginService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catalogId");
        if (!StringUtils.isEmpty(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId)) {
            wrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            wrapper.ge("price", min);
        }
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max)) {
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(new BigDecimal("0")) == 1) {
                    //大于零才可以拼接最大值范围
                    wrapper.le("price", max);
                }
            } catch (Exception e) {
            }
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {
        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return list;
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //获取sku基本信息
            SkuInfoEntity info = getById(skuId);
            skuItemVo.setInfo(info);
            return info;
        }, executor);
        //以下三次异步任务都是基于获取sku基本信息任务，并且三个任务是并列的
        CompletableFuture<Void> saleAttrsFuture = infoFuture.thenAcceptAsync((res) -> {
            //根据获取sku基本信息任务查询到的基本信息的spuId，来获取spu的所有销售属性组合
            List<itemSaleAttrsVo> saleAttrs = saleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttrs(saleAttrs);
        }, executor);
        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            //根据获取sku基本信息务查询到的基本信息的spuId，来获取spu的介绍信息
            SpuInfoDescEntity descEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesc(descEntity);
        }, executor);
        CompletableFuture<Void> baseAttrsFuture = infoFuture.thenAcceptAsync((res) -> {
            //根据获取sku基本信息任务查询到的基本信息的spuId和catalogId,获取spu的规格参数信息【其中的分组信息可以通过sku基本信息中的分类id来确定】
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.
                    getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
        }, executor);
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            //获取sku图片信息和获取sku基本信息是并列执行的
            List<SkuImagesEntity> images = imagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        }, executor);
        //查询当前商品是否参与秒杀优惠
        CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
            R r = seckillFeginService.getSkuSeckillInfo(skuId);
            if (r.getCode() == 0) {
                SeckillSkuRelationVo skuRelationVo = r.getData(new TypeReference<SeckillSkuRelationVo>() {
                });
                skuItemVo.setSeckillSkuRelationVo(skuRelationVo);
            }
        }, executor);
        //等待所有任务都完成【可以不用等待infoFuture完成，因为有三个任务是基于infoFuture的，它们其中之一完成说明infoFuture早完成了】
        CompletableFuture.allOf(imageFuture,descFuture,saleAttrsFuture,baseAttrsFuture,seckillFuture).get();
        return skuItemVo;
    }

}