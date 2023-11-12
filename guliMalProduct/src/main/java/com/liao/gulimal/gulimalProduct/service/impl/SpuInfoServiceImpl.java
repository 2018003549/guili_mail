package com.liao.gulimal.gulimalProduct.service.impl;

import com.liao.common.to.MemberPrice;
import com.liao.common.to.SkuReductionTo;
import com.liao.common.to.SpuBoundTo;
import com.liao.common.utils.R;
import com.liao.gulimal.gulimalProduct.dao.SpuInfoDescDao;
import com.liao.gulimal.gulimalProduct.entity.*;
import com.liao.gulimal.gulimalProduct.fegin.CouponFeginService;
import com.liao.gulimal.gulimalProduct.service.*;
import com.liao.gulimal.gulimalProduct.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.Query;

import com.liao.gulimal.gulimalProduct.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDescService descService;
    @Autowired
    SpuImagesService imagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponFeginService couponFeginService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SPUSaveVo spuInfoVo) {
//        1. 保存spu基本信息
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoVo, spuInfoEntity);
        //SPUSaveVo没有时间相关信息，所以这里手动给两个时间字段赋值
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);
//        2. 保存spu的描述图片
        List<String> decript = spuInfoVo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfoEntity.getId());
        descEntity.setDecript(String.join(",", decript));//将所有图片用","分割
        descService.saveSpuInfoDesc(descEntity);
//        3. 保存spu的图片集
        List<String> images = spuInfoVo.getImages();
        imagesService.saveImages(spuInfoEntity.getId(), images);//给指定商品录入图片集数据
//        4. 保存spu的规格参数
        List<BaseAttrs> baseAttrs = spuInfoVo.getBaseAttrs();
        List<ProductAttrValueEntity> attrValueEntities = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());//获取属性名称
            valueEntity.setAttrName(attrEntity.getAttrName());
            valueEntity.setAttrId(attr.getAttrId());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(spuInfoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(attrValueEntities);
//        5. 保存spu的积分信息【跨服务】`sms_spu_bounds`
        Bounds bounds = spuInfoVo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r1 = couponFeginService.saveSpuBounds(spuBoundTo);
        if(r1.getCode()!=0){
            log.error("远程保存spu积分信息失败");
        }
//        6. 保存当前spu对应的所有sku信息
        List<Skus> skus = spuInfoVo.getSkus();
        if(skus!=null&&skus.size()!=0){
            skus.forEach(sku->{
                //1. sku的基本信息
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku,skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                String defaultImg="";
                for (Images image : sku.getImages()) {
                    if(image.getDefaultImg()==1){
                        //遍历图片集，找到默认图片
                        defaultImg=image.getImgUrl();
                        break;
                    }
                }
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.save(skuInfoEntity);
                //2. sku的图片信息
                //调用mybatis的插入方法，会将自增长的id赋给实体
                //TODO 没有图片路径的图片信息，无需保存
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imagesEntities = sku.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity->{
                    //返回false就会被剔除
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);//批量保存所有图片信息
                //3. sku销售属性信息 `pms_sku_sale_attr_value`
                List<Attr> attrs=sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);
                    return attrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                //4. sku的优惠、满减信息【跨库】`gulimall sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price`
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if(skuReductionTo.getFullCount()>0||skuReductionTo.getFullPrice().compareTo(new BigDecimal(0))==1
                ||skuReductionTo.getMemberPrice()!=null||skuReductionTo.getMemberPrice().size()>0){
                    R r = couponFeginService.saveSkuReduction(skuReductionTo);
                    if(r.getCode()!=0){
                        log.error("远程保存sku信息失败");
                    }
                }
            });
        }



    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                //status and (id=1 or spu_name like xxx)
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)){
            wrapper.eq("catalog_id",catelogId);//数据库字段名写错了，无语😶
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }


}