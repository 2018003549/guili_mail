package com.liao.gulimall.gulimallthirdpart;

import com.aliyun.oss.OSSClient;
import com.liao.gulimall.gulimallthirdpart.component.SmsComponent;
import com.liao.gulimall.gulimallthirdpart.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class GulimallThirdPartApplicationTests {
    @Autowired
    OSSClient ossClient;
    @Autowired
    SmsComponent smsComponent;
    @Test
    void testSendSms() {
        smsComponent.sendSmsCode("13265114494","377099","3");
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
