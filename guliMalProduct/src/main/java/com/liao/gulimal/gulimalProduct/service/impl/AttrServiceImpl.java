package com.liao.gulimal.gulimalProduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.liao.gulimal.gulimalProduct.dao.AttrAttrgroupRelationDao;
import com.liao.gulimal.gulimalProduct.dao.AttrGroupDao;
import com.liao.gulimal.gulimalProduct.dao.CategoryDao;
import com.liao.gulimal.gulimalProduct.entity.AttrAttrgroupRelationEntity;
import com.liao.gulimal.gulimalProduct.entity.AttrGroupEntity;
import com.liao.gulimal.gulimalProduct.entity.CategoryEntity;
import com.liao.gulimal.gulimalProduct.service.CategoryService;
import com.liao.gulimal.gulimalProduct.vo.AttrGroupRelationVo;
import com.liao.gulimal.gulimalProduct.vo.AttrRespVo;
import com.liao.gulimal.gulimalProduct.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.Query;

import com.liao.gulimal.gulimalProduct.dao.AttrDao;
import com.liao.gulimal.gulimalProduct.entity.AttrEntity;
import com.liao.gulimal.gulimalProduct.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    AttrAttrgroupRelationDao relationDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    CategoryService categoryService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);//给相同属性进行复制
        //1.保存基本数据
        this.save(attrEntity);
        //2.保存关联关系
        if(attr.getAttrType()==1){
            //基本属性才需要设置分组关联
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());//新增完的属性
            relationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_type","base".equalsIgnoreCase(type)?1:0);//无论是查询所有还是查询指定id都要先区分是销售还是基本属性
        if (catelogId != 0) {
            wrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((queryWrapper) -> {
                queryWrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();//获取page数据，对数据进行重新处理
        List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            //设置分类名和分组名(分组名要级联查询)
            AttrAttrgroupRelationEntity relation = relationDao.
                    selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().
                            eq("attr_id", attrEntity.getAttrId()));
            if (relation != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relation.getAttrGroupId());
                if(attrGroupEntity!=null){
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());
        pageUtils.setList(respVos);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo respVo=new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);//先查询当前属性的基本信息
        BeanUtils.copyProperties(attrEntity,respVo);
        //设置当前属性的分组信息
        AttrAttrgroupRelationEntity attrGroupRelation = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_id", attrEntity.getAttrId()));
        if(attrGroupRelation!=null){
            respVo.setAttrGroupId(attrGroupRelation.getAttrGroupId());
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupRelation.getAttrGroupId());
            if(attrEntity!=null){
                respVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }
        //设置当前属性的分类路径
        Long catelogId = attrEntity.getCatelogId();
        Long[] categoryPath = categoryService.findCategoryPath(catelogId);//查询当前分类id的完整路径
        respVo.setCatelogPath(categoryPath);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if(categoryEntity!=null){
            respVo.setCatelogName(categoryEntity.getName());
        }
        return respVo;
    }
    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        //1.先更新基本数据
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);
        //2.修改分组关联
        if(attrEntity.getAttrType()==1){
            //首先先查当前属性是否关联过分组
            Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attr.getAttrId());
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            if (count>0) {
                //如果有关联分组就更新
                relationDao.update(relationEntity,new UpdateWrapper<AttrAttrgroupRelationEntity>().
                        eq("attr_id",attrEntity.getAttrId()));
            }else {
                //如果每关联分组就新增
                relationDao.insert(relationEntity);
            }
        }
        //TODO 3.修改分类关联
    }

    /**
     * 根据分组id找到所有关联的属性
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        //根据attrgroupId查询到关联表中所有记录
        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.
                selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().
                eq("attr_group_id", attrgroupId)
        );
        //将所有记录中的属性id封装成一个集合
        List<Long> attrIds = relationEntities.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        if(attrIds==null||attrIds.size()==0){
            return null;
        }
        List<AttrEntity> attrEntities = this.listByIds(attrIds);
        return attrEntities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] relationVos) {
        //将当前接收的数据类型转换成relationDao绑定的类型
        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(relationVos).stream().map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(entities);//批量删除所以满足条件的属性
    }

    /**
     * 获取当前分组没有关联的所有基本属性
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //1.当前分组只能关联自己所属分类的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //2.当前分组只能关联别的分组没有关联过的属性
        //2.1找到当前分类下的所有分组信息
        List<AttrGroupEntity> otherGroups = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));
        List<Long> otherGroupIds = otherGroups.stream().map((group) -> {
            return group.getAttrGroupId();
        }).collect(Collectors.toList());
        //2.2找到这些分组关联的属性
        List<AttrAttrgroupRelationEntity> relationAttr = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .in("attr_group_id", otherGroupIds));
        List<Long> attrIds = relationAttr.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        //2.3从当前分类的所有属性中查询“没有被关联过“的基本属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId).eq("attr_type",1);
        if(attrIds!=null&&attrIds.size()>0){
            //如果有关联属性就排除
            wrapper.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

}