package com.liao.gulimal.gulimalmember.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liao.common.utils.HttpUtils;
import com.liao.gulimal.gulimalmember.exception.PhoneException;
import com.liao.gulimal.gulimalmember.exception.UserNameExistException;
import com.liao.gulimal.gulimalmember.vo.MemberLoginVo;
import com.liao.gulimal.gulimalmember.vo.MemberRegistVo;
import com.liao.gulimal.gulimalmember.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.Query;

import com.liao.gulimal.gulimalmember.dao.MemberDao;
import com.liao.gulimal.gulimalmember.entity.MemberEntity;
import com.liao.gulimal.gulimalmember.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setLevelId(1l);//设置默认会员等级
        //检查用户名和手机号的是否唯一,可以使用异常机制让上层感知到
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUserName());
        //密码需要加密存储
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
        this.baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneException {
        Integer phoneCount = this.baseMapper.
                selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(phoneCount>0){
            throw new PhoneException() ;
        }
    }

    @Override
    public void checkUserNameUnique(String username) throws UserNameExistException {
        Integer count = this.baseMapper.
                selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if(count>0){
            throw new PhoneException() ;
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        //1.先去数据库查询是否有该用户
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().
                eq("username", vo.getLoginAccount()).or().eq("mobile", vo.getLoginAccount()));
        if(entity==null){
            //没有该用户
            return null;
        }else {
            //2.有该用户就校验密码
            String password = entity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //3.密码匹配
            boolean matches = passwordEncoder.matches(vo.getPassword(), password);
            if(matches){
                return entity;
            }else {
                return null;
            }
        }
    }

    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {
        //具有登录和注册合并逻辑
        String uid = socialUser.getUid();//获取社交用户的唯一标识
        //判断当前社交用户是否已经注册过本系统
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if(memberEntity!=null){
            //该用户注册过，需要给它更新本次登录的临时令牌和过期时间
            MemberEntity update=new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.updateById(update);
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        }else {
            //没有查到当前用户，就说明没有在本系统注册过，本次登录需要进行注册
            MemberEntity regist = new MemberEntity();
            try {
                //查询当前社交用户的社交账号信息，查询基本信息不能影响到注册操作，所以要try
                HashMap<String, String> query = new HashMap<>();
                query.put("access_token",socialUser.getAccess_token());
                query.put("uid",socialUser.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com",
                        "/2/users/show.json", "get", new HashMap<>(), query);
                if(response.getStatusLine().getStatusCode()==200){
                    //查询成功
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    regist.setNickname(name);
                    regist.setGender("m".equalsIgnoreCase(gender)?1:0);
                }
            }catch (Exception e){

            }
            regist.setSocialUid(socialUser.getUid());
            regist.setAccessToken(socialUser.getAccess_token());
            regist.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.insert(regist);
            return regist;
        }
    }
}