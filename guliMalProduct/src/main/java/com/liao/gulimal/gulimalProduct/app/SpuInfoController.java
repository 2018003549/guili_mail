package com.liao.gulimal.gulimalProduct.app;

import java.util.Arrays;
import java.util.Map;

import com.liao.gulimal.gulimalProduct.vo.SPUSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.liao.gulimal.gulimalProduct.entity.SpuInfoEntity;
import com.liao.gulimal.gulimalProduct.service.SpuInfoService;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.R;



/**
 * spu信息
 *
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:11:14
 */
@RestController
@RequestMapping("gulimalProduct/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;
    @PostMapping("/{spuId}/up")
    public R list(@PathVariable("spuId") Long spuId){
        spuInfoService.up(spuId);
        return R.ok();
    }
    @GetMapping("/skuId/{id}")
    public R getSpuInfoBySkuId(@PathVariable("id")Long skuId){
        SpuInfoEntity spuInfoEntity=spuInfoService.getSpuInfoBySkuId(skuId);
        return R.ok().put("data",spuInfoEntity);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageByCondition(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SPUSaveVo spuInfo){
        spuInfoService.saveSpuInfo(spuInfo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
