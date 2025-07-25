package com.shemuel.timeline.config;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Service
public class S3Service {

    @Value("${s3.bucketName}")
    private String bucketName;

    // base/timeline/
    @Value("${s3.base-path}")
    private String path;
    

    @Value("${dromara.x-file-storage.thumbnail-suffix}")
    private String thumbnailSuffix;

    @Autowired
    private AmazonS3 s3Client;


    // 上传文件
    public String uploadFile(String key, File file) {
       s3Client.putObject(new PutObjectRequest( bucketName, path + key , file));
        return getFileSignedUrl( key,1000000);
    }


    // 上传文件并指定文件名
    public String uploadFileWithKey(String key, InputStream inputStream) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(inputStream.available());
        s3Client.putObject(new PutObjectRequest(bucketName, path + key, inputStream, metadata));
        return getFileSignedUrl( key,1000000);
    }

    // 删除文件
    public void deleteFile(String key) {
        s3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
    }

    // 获取文件的公共访问链接
    public String getFilePublicUrl(String key) {
        return s3Client.getUrl(bucketName, key).toString();
    }

    // 获取文件的私有访问链接（带签名）
    public String getFileSignedUrl(String key, long expirationTime) {
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest( bucketName, path + key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expirationDate);
        URL signedUrl = s3Client.generatePresignedUrl(request);
        return signedUrl.toString();
    }
    
    /**
     * 获取文件缩略图的访问链接（带签名）
     * @param key 文件的key
     * @param expirationTime 过期时间（毫秒）
     * @return 缩略图的签名URL
     */
    public String getThumbnailSignedUrl(String key, long expirationTime) {
        // 构建缩略图的key，通常是在原文件名后添加后缀
        String thumbnailKey = getThumbnailKey(key);
        
        // 检查缩略图是否存在
        if (s3Client.doesObjectExist(bucketName, thumbnailKey)) {
            return getFileSignedUrl(thumbnailKey, expirationTime);
        }
        
        // 如果缩略图不存在，返回原图链接
        return getFileSignedUrl(key, expirationTime);
    }
    
    /**
     * 获取缩略图的key
     * @param originalKey 原始文件的key
     * @return 缩略图的key
     */
    private String getThumbnailKey(String key) {
        int lastDotIndex = key.lastIndexOf('.');
        if (lastDotIndex > 0) {
            // 在文件扩展名前插入缩略图后缀
            return key.substring(0, lastDotIndex) + thumbnailSuffix;
        } else {
            // 如果没有扩展名，直接添加缩略图后缀
            return key + thumbnailSuffix;
        }
    }
}
