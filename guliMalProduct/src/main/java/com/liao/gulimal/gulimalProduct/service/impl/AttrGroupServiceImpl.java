package com.liao.gulimal.gulimalProduct.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
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

}