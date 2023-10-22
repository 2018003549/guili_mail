package com.liao.gulimal.gulimalmember.dao;

import com.liao.gulimal.gulimalmember.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:21:15
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
