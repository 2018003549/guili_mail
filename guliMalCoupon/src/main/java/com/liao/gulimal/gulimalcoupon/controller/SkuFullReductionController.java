package com.liao.gulimal.gulimalcoupon.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.liao.common.to.MemberPrice;
import com.liao.common.to.SkuReductionTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.liao.gulimal.gulimalcoupon.entity.SkuFullReductionEntity;
import com.liao.gulimal.gulimalcoupon.service.SkuFullReductionService;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.R;



/**
 * 商品满减信息
 *
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 19:53:15
 */
@RestController
@RequestMapping("gulimalcoupon/skufullreduction")
public class SkuFullReductionController {
    @Autowired
    private SkuFullReductionService skuFullReductionService;
    @PostMapping("/saveInfo")
    public R list(@RequestBody SkuReductionTo reductionTo){
        skuFullReductionService.saveSkuReduction(reductionTo);
        return R.ok();
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuFullReductionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SkuFullReductionEntity skuFullReduction = skuFullReductionService.getById(id);

        return R.ok().put("skuFullReduction", skuFullReduction);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuFullReductionEntity skuFullReduction){
		skuFullReductionService.save(skuFullReduction);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuFullReductionEntity skuFullReduction){
		skuFullReductionService.updateById(skuFullReduction);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		skuFullReductionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
