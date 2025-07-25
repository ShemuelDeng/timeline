package com.shemuel.timeline;

import com.shemuel.timeline.config.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;

/**
 * @author ：GuangXiZhong
 * @date ：Created in 2025/4/23 16:06
 * @description：
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class S3Test {

    @Autowired
    private S3Service s3Service;

    /**
     * S3测试
     */
    @Test
    public void testS3() {
        File file = new File("D:\\code\\timeline\\timeline-java\\src\\main\\resources\\111.png");

        System.out.println(s3Service.uploadFile("dsx"+System.currentTimeMillis()+".png", file));
    }
}
