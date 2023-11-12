package com.liao.gulimal.gulimalProduct.service.impl;

import com.liao.gulimal.gulimalProduct.dao.AttrAttrgroupRelationDao;
import com.liao.gulimal.gulimalProduct.entity.AttrEntity;
import com.liao.gulimal.gulimalProduct.service.AttrAttrgroupRelationService;
import com.liao.gulimal.gulimalProduct.service.AttrService;
import com.liao.gulimal.gulimalProduct.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.Query;

import com.liao.gulimal.gulimalProduct.dao.AttrGroupDao;
import com.liao.gulimal.gulimalProduct.entity.AttrGroupEntity;
import com.liao.gulimal.gulimalProduct.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        if(catelogId==0){
            //如果没有节点编号就是查询所有
            IPage<AttrGroupEntity> page=this.page(new Query<AttrGroupEntity>().getPage(params)
                    ,new QueryWrapper<AttrGroupEntity>());
            return new PageUtils(page);
        }else {
            Object key =  params.get("key");//检索条件
            //select * from 表 where cateLog_=id ? and (条件1 or 条件2)
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>().
                    eq("catelog_id",catelogId);
            if(!StringUtils.isEmpty(key)){
                //有查询条件
                wrapper.and((obj)->{
                    obj.eq("attr_group_id",key).or().
                            like("attr_group_name",key);//这个like是首尾都有%的
                });
            }
            IPage<AttrGroupEntity> page=this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCateLogId(Long catelogId) {
        //1.查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));
        //查询每个分组下的所有属性
        List<AttrGroupWithAttrsVo> attrsVos = attrGroupEntities.stream().map(attrGroupEntity -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(attrGroupEntity, attrGroupWithAttrsVo);
            //查询当前分组下的所有属性
            List<AttrEntity> relationAttr = attrService.getRelationAttr(attrGroupEntity.getAttrGroupId());
            attrGroupWithAttrsVo.setAttrs(relationAttr);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());
        return attrsVos;
    }

}