package com.liao.gulimal.gulimalcoupon.service.impl;

import com.liao.common.to.MemberPrice;
import com.liao.common.to.SkuReductionTo;
import com.liao.gulimal.gulimalcoupon.entity.MemberPriceEntity;
import com.liao.gulimal.gulimalcoupon.entity.SkuLadderEntity;
import com.liao.gulimal.gulimalcoupon.service.MemberPriceService;
import com.liao.gulimal.gulimalcoupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.Query;

import com.liao.gulimal.gulimalcoupon.dao.SkuFullReductionDao;
import com.liao.gulimal.gulimalcoupon.entity.SkuFullReductionEntity;
import com.liao.gulimal.gulimalcoupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    SkuLadderService skuLadderService;
    @Autowired
    MemberPriceService memberPriceService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo reductionTo) {
        //1.保存sku的优惠、满减信息`gulimall sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price`
        //阶梯价格
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(reductionTo,skuLadderEntity);
        if(reductionTo.getFullCount()>0){
            skuLadderService.save(skuLadderEntity);
        }
        //满减信息
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(reductionTo,skuFullReductionEntity);
        if(reductionTo.getFullPrice().compareTo(new BigDecimal("0"))==1){
            this.save(skuFullReductionEntity);
        }
        //会员价格
        List<MemberPrice> memberPrices = reductionTo.getMemberPrice();
        if(memberPrices!=null&&memberPrices.size()>0){
            List<MemberPriceEntity> memberPriceEntities = memberPrices.stream().map(memberPrice -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                //由于属性名都没一一对应，所以用不了BeanUtils
                memberPriceEntity.setMemberPrice(memberPrice.getPrice());
                memberPriceEntity.setMemberLevelId(memberPrice.getId());
                memberPriceEntity.setMemberLevelName(memberPrice.getName());
                memberPriceEntity.setSkuId(reductionTo.getSkuId());
                memberPriceEntity.setAddOther(1);
                return memberPriceEntity;
            }).filter(item->{
                return item.getMemberPrice().compareTo(new BigDecimal("0"))==1;
            }).collect(Collectors.toList());
            memberPriceService.saveBatch(memberPriceEntities);
        }
    }

}