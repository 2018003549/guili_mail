package com.liao.gulimal.gulimalcoupon.service.impl;

import com.liao.gulimal.gulimalcoupon.entity.SeckillSkuRelationEntity;
import com.liao.gulimal.gulimalcoupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.Query;

import com.liao.gulimal.gulimalcoupon.dao.SeckillSessionDao;
import com.liao.gulimal.gulimalcoupon.entity.SeckillSessionEntity;
import com.liao.gulimal.gulimalcoupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {
    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLates3DaySession() {
        //1.查出最近三天开始的所有秒杀活动
        List<SeckillSessionEntity> list = this.list(new QueryWrapper<SeckillSessionEntity>().
                between("start_time", startTime(), endTime()));
        if(list!=null&&list.size()>0){
            //2.查出所有秒杀活动关联的商品
            List<SeckillSessionEntity> collect = list.stream().map(session -> {
                List<SeckillSkuRelationEntity> skus = seckillSkuRelationService.list(
                        new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", session.getId()));
                session.setRelationSkus(skus);
                return session;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;//没有秒杀活动，或者所有秒杀活动都没有关联商品
    }
    public String startTime(){
        //计算起始时间
        LocalDate now = LocalDate.now();//获取当前时间，精确到日
        //年月日和时分秒拼接
        LocalDateTime start=LocalDateTime.of(now, LocalTime.MIN);//LocalTime.MIN表示00：00:00
        String format = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }
    public String endTime(){
        //计算结束时间
        LocalDate now = LocalDate.now();
        LocalDate plusDays = now.plusDays(2);//当前时间+2天
        LocalDateTime end=LocalDateTime.of(plusDays, LocalTime.MAX);//LocalTime.MAX表示23：59：59E
        String format = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }
}