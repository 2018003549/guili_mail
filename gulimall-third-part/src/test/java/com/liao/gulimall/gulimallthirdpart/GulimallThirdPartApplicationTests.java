package com.liao.gulimall.gulimallthirdpart;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class GulimallThirdPartApplicationTests {
    @Autowired
    OSSClient ossClient;
    @Test
    void contextLoads() {

    }
    @Test
    public void testUpload() throws FileNotFoundException {
        // 上传文件流。
        InputStream inputStream = new FileInputStream("E:\\万华镜\\银河足球队\\Soccer Spirits\\Soccer Spirits\\0004_10101_CS_out.png");
        ossClient.putObject("gulimall-liaopeiwei","测试新密钥.png", inputStream);
        // 关闭ossclient。
        ossClient.shutdown();
        System.out.println("上传成功");
    }
}
