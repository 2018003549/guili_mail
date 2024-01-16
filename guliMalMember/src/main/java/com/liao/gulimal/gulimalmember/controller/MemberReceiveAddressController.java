package com.liao.gulimal.gulimalmember.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.liao.gulimal.gulimalmember.entity.MemberReceiveAddressEntity;
import com.liao.gulimal.gulimalmember.service.MemberReceiveAddressService;
import com.liao.common.utils.PageUtils;
import com.liao.common.utils.R;



/**
 * 会员收货地址
 *
 * @author liao
 * @email sunlightcs@gmail.com
 * @date 2023-10-22 14:21:15
 */
@RestController
@RequestMapping("gulimalmember/memberreceiveaddress")
public class MemberReceiveAddressController {
    @Autowired
    private MemberReceiveAddressService memberReceiveAddressService;
    @GetMapping("/{memberId}/addresses")
    public List<MemberReceiveAddressEntity> getAddress(@PathVariable("memberId") Long memberId){
        List<MemberReceiveAddressEntity> addresses=memberReceiveAddressService.getAddresses(memberId);
        return addresses;
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberReceiveAddressService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberReceiveAddressEntity memberReceiveAddress = memberReceiveAddressService.getById(id);
        return R.ok().put("memberReceiveAddress", memberReceiveAddress);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberReceiveAddressEntity memberReceiveAddress){
		memberReceiveAddressService.save(memberReceiveAddress);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberReceiveAddressEntity memberReceiveAddress){
		memberReceiveAddressService.updateById(memberReceiveAddress);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberReceiveAddressService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
