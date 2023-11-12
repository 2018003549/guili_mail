package com.liao.gulimal.gulimalProduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.liao.gulimal.gulimalProduct.entity.AttrEntity;
import com.liao.gulimal.gulimalProduct.service.AttrService;
import com.liao.gulimal.gulimalProduct.service.CategoryService;
import com.liao.gulimal.gulimalProduct.vo.AttrGroupRelationVo;
import com.liao.gulimal.gulimalProduct.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.liao.gulimal.gulimalProduct.entity.AttrGroupEntity;
import com.liao.gulimal.gulimalProduct.service.AttrGroupService;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.R;



/**
 * 属性分组
 *
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:11:14
 */
@RestController
@RequestMapping("gulimalProduct/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrService attrService;
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId")Long attrgroupId){
        //根据分组id找到组内关联的所有属性
        List<AttrEntity> entityList=attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data",entityList);
    }
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId")Long attrgroupId,
                            @RequestParam Map<String, Object> params){
        //根据分组id找到组内关联的所有属性
        PageUtils page=attrService.getNoRelationAttr(params,attrgroupId);
        return R.ok().put("page",page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catelogId")Long catelogId){
        PageUtils page = attrGroupService.queryPage(params, catelogId);
        return R.ok().put("page", page);
    }
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId")Long catelogId){
        List<AttrGroupWithAttrsVo> vos=attrGroupService.getAttrGroupWithAttrsByCateLogId(catelogId);
        return R.ok().put("data",vos);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();//获取当前分类的id
        Long[] path=categoryService.findCategoryPath(catelogId);//根据当前分类的id找到其路径
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }
    @PostMapping("attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] relationVos){
        attrService.deleteRelation(relationVos);
        return R.ok();
    }
}
