package com.shemuel.timeline.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：GuangXiZhong
 * @date ：Created in 2025/6/10 15:21
 * @description：
 */
@Configuration
public class S3ClientConfig {

    @Value("${s3.accessKey}")
    private String accessKey;

    @Value("${s3.secretKey}")
    private String secretKey;

    @Value("${s3.endpoint}")
    private String endpoint;

    @Value("${s3.region}")
    private String region;

    @Bean
    public AmazonS3 amazonS3Client() {
        // 初始化 S3 客户端
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(endpoint, region);
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}
