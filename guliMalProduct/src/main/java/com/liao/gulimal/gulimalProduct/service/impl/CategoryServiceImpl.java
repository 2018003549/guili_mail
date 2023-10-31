package com.liao.gulimal.gulimalProduct.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.Query;

import com.liao.gulimal.gulimalProduct.dao.CategoryDao;
import com.liao.gulimal.gulimalProduct.entity.CategoryEntity;
import com.liao.gulimal.gulimalProduct.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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
        List<CategoryEntity> level1 = list.stream().filter(categoryEntity->//只有一个参数，所有小括号可以删掉
              categoryEntity.getParentCid() == 0//只有一行语句，{}和return还有;可以删掉
        ).map(menu->{
            menu.setChildren(getChildrens(menu,list));//获取所有菜单的子菜单并且存储
            return menu;
        }).sorted((menu1,menu2)->
                (menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort())
        ).collect(Collectors.toList());
        return level1;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1、检查当前删除的菜单是否被其它菜单引用
        //使用逻辑删除，不是删除记录，而是修改该记录的标识变为不可用
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 获取当前菜单的子菜单
     * @param menu 当前菜单
     * @param list 所有菜单
     * @return
     */
    public List<CategoryEntity> getChildrens(  CategoryEntity menu,List<CategoryEntity> list){
        List<CategoryEntity> children= list.stream().filter(
                //子菜单对应的parentId等于当前菜单的id
                categoryEntity -> categoryEntity.getParentCid().equals(menu.getCatId())
        ).map(categoryEntity -> {
            //递归查询子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity,list));
            return categoryEntity;
        }).sorted((menu1,menu2)->
                (menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort())
        ).collect(Collectors.toList());
        return children;
    }
}