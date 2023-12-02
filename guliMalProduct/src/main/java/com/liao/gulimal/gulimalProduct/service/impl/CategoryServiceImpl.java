package com.liao.gulimal.gulimalProduct.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.liao.gulimal.gulimalProduct.service.CategoryBrandRelationService;
import com.liao.gulimal.gulimalProduct.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.Query;

import com.liao.gulimal.gulimalProduct.dao.CategoryDao;
import com.liao.gulimal.gulimalProduct.entity.CategoryEntity;
import com.liao.gulimal.gulimalProduct.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查询出所有数据，没有查询条件就是查询所有
        List<CategoryEntity> list = baseMapper.selectList(null);
        //组装成父子结构
        //1.找到所有一级分类,父分类id=0
        //List<CategoryEntity> level1 = list.stream().filter((categoryEntity) -> {
        //return categoryEntity.getParentCid() == 0;
        //}).collect(Collectors.toList());//把所有父分类id==0的数据过滤出来放在一个集合
        List<CategoryEntity> level1 = list.stream().filter(categoryEntity ->//只有一个参数，所有小括号可以删掉
                categoryEntity.getParentCid() == 0//只有一行语句，{}和return还有;可以删掉
        ).map(menu -> {
            menu.setChildren(getChildrens(menu, list));//获取所有菜单的子菜单并且存储
            return menu;
        }).sorted((menu1, menu2) ->
                (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort())
        ).collect(Collectors.toList());
        return level1;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1、检查当前删除的菜单是否被其它菜单引用
        //使用逻辑删除，不是删除记录，而是修改该记录的标识变为不可用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCategoryPath(Long catelogId) {
        LinkedList<Long> paths = new LinkedList<>();
        findParentPaths(catelogId, paths);//递归寻找当前分类的路径
        return paths.toArray(new Long[paths.size()]);
    }

    @Transactional
    @CacheEvict(value = "category",allEntries = true)
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }
    //指定数据需要放入的缓存分区【推荐按照业务类型分】
    //表示当前方法的结果需要缓存，如果缓存中有，不用调用方法，如果缓存中没有就调用方法并将结果放入缓存
    @Cacheable(value = {"category"},key = "#root.methodName.substring(3)")
    @Override
    public List<CategoryEntity> getLevelFirst() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(
                new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    /**
     * 整合springCache查询三级分类数据
     * @return
     */
    @Override
    @Cacheable(value = "category",key = "#root.methodName.substring(3)")
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        //查询所有
        List<CategoryEntity> selectList = baseMapper.selectList(new QueryWrapper<>(null));
        //1.查出所有一级分类
        List<CategoryEntity> levelFirst = getParentId(selectList, 0l);
        //封装数据
        Map<String, List<Catelog2Vo>> map = levelFirst.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //2.查询每个一级分类的所有二级分类
            List<CategoryEntity> categoryEntities = getParentId(selectList, v.getCatId());//将当前分类的id作为父id，去查询其子节点
            List<Catelog2Vo> catelog2VoList = null;
            if (categoryEntities != null) {
                catelog2VoList = categoryEntities.stream().map(level2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo
                            (v.getCatId().toString(), null, level2.getCatId().toString(), level2.getName());
                    //3.查询当前二级分类的三级分类
                    List<CategoryEntity> catelog3List = getParentId(selectList, level2.getCatId());
                    if (catelog3List != null) {
                        //封装成指定格式
                        List<Catelog2Vo.Catelog3Vo> catelog3VoList = catelog3List.stream().map(level3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo
                                    (level2.getCatId().toString(),
                                            level3.getCatId().toString(), level3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catelog3VoList);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2VoList;
        }));
        return map;
    }
    public Map<String, List<Catelog2Vo>> getCatelogJsonByRedisLock() {
        //1.查询缓存是否有分类数据
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            System.out.println("预先查询数据库");
            //1.1如果缓存中没有就要查询数据库
            Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatelogJsonFromDb();
            //1.2将查询到的数据转成json字符串放入缓存【json跨语言跨平台兼容】
            catalogJSON = JSON.toJSONString(catelogJsonFromDb);
            redisTemplate.opsForValue().set("catalogJSON", catalogJSON);
            return catelogJsonFromDb;
        }
        //2.将缓存获取到的json字符串转成map对象
        Map<String, List<Catelog2Vo>> result =
                JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
        return result;
    }

    /**
     * 从数据库查询并封装三级分类数据
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDb() {
        //得到锁之后，再去缓存中查询一次【双检】
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            System.out.println("缓存中查询到了");
            Map<String, List<Catelog2Vo>> result =
                    JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                    });
            return result;
        }
        System.out.println("查询数据库.....");
        //查询所有
        List<CategoryEntity> selectList = baseMapper.selectList(new QueryWrapper<>(null));
        //1.查出所有一级分类
        List<CategoryEntity> levelFirst = getParentId(selectList, 0l);
        //封装数据
        Map<String, List<Catelog2Vo>> map = levelFirst.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //2.查询每个一级分类的所有二级分类
            List<CategoryEntity> categoryEntities = getParentId(selectList, v.getParentCid());
            List<Catelog2Vo> catelog2VoList = null;
            if (categoryEntities != null) {
                catelog2VoList = categoryEntities.stream().map(level2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo
                            (v.getCatId().toString(), null, level2.getCatId().toString(), level2.getName());
                    //3.查询当前二级分类的三级分类
                    List<CategoryEntity> catelog3List = getParentId(selectList, level2.getParentCid());
                    if (catelog3List != null) {
                        //封装成指定格式
                        List<Catelog2Vo.Catelog3Vo> catelog3VoList = catelog3List.stream().map(level3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo
                                    (level2.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catelog3VoList);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2VoList;
        }));
        catalogJSON = JSON.toJSONString(map);
        redisTemplate.opsForValue().set("catalogJSON", catalogJSON);
        return map;
    }

    public List<CategoryEntity> getParentId(List<CategoryEntity> selectList, Long parentCid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> {
            return item.getParentCid() == parentCid;
        }).collect(Collectors.toList());
        return collect;
    }

    private List<Long> findParentPaths(Long catelogId, LinkedList<Long> paths) {
        CategoryEntity byId = this.getById(catelogId);//获取当前分类的所有信息
        //先收集当前节点id
        paths.addFirst(catelogId);//这里用头插是因为找路径是从末尾往起点找的，最后得到的是相反路径
        if (byId.getParentCid() != 0) {
            //有父亲就继续溯源
            findParentPaths(byId.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 获取当前菜单的子菜单
     *
     * @param menu 当前菜单
     * @param list 所有菜单
     * @return
     */
    public List<CategoryEntity> getChildrens(CategoryEntity menu, List<CategoryEntity> list) {
        List<CategoryEntity> children = list.stream().filter(
                //子菜单对应的parentId等于当前菜单的id
                categoryEntity -> categoryEntity.getParentCid().equals(menu.getCatId())
        ).map(categoryEntity -> {
            //递归查询子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, list));
            return categoryEntity;
        }).sorted((menu1, menu2) ->
                (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort())
        ).collect(Collectors.toList());
        return children;
    }
}