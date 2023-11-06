package com.liao.gulimal.gulimalProduct;

import com.liao.gulimal.gulimalProduct.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;
import java.util.Arrays;

@Slf4j
@SpringBootTest
class GuliMalProductApplicationTests {

    @Autowired
    CategoryService categoryService;
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
