package com.liao.gulimal.gulimalProduct;

import com.liao.gulimal.gulimalProduct.dao.AttrGroupDao;
import com.liao.gulimal.gulimalProduct.dao.SkuSaleAttrValueDao;
import com.liao.gulimal.gulimalProduct.service.AttrGroupService;
import com.liao.gulimal.gulimalProduct.service.CategoryService;
import com.liao.gulimal.gulimalProduct.vo.SkuItemVo;
import com.liao.gulimal.gulimalProduct.vo.SpuItemAttrGroupVo;
import com.liao.gulimal.gulimalProduct.vo.itemSaleAttrsVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
class GuliMalProductApplicationTests {

    @Autowired
    CategoryService categoryService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Test
    public void testGetSaleAttr(){
        List<itemSaleAttrsVo> saleAttrsBySpuId = skuSaleAttrValueDao.getSaleAttrsBySpuId(13l);
        System.out.println(saleAttrsBySpuId);
    }
    @Test
    public void testGetSpuGroupInfo(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(10l, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }
    @Test
    public void testRedissonClient(){
        System.out.println(redissonClient);
    }
    @Test
    public void testStringRedisTemplate(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello","world"+ UUID.randomUUID());
        String hello = ops.get("hello");
        System.out.println(hello);
    }
    @Test
    public void testFindPath() {
        Long[] categoryPath = categoryService.findCategoryPath(225L);
        log.info("完整路径:{}", Arrays.asList(categoryPath));
    }

    @Test
    public void testUpload() throws FileNotFoundException {
//        // 上传文件流。
//        InputStream inputStream = new FileInputStream("C:\\Users\\LPW\\Pictures\\Saved Pictures\\1.jpg");
//        ossClient.putObject("gulimall-liaopeiwei","testAlibaba.png", inputStream);
//        // 关闭ossclient。
//        ossClient.shutdown();
//        System.out.println("上传成功");
    }

    @Test
    void contextLoads() {
    }

}
