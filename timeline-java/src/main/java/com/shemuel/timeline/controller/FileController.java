package com.shemuel.timeline.controller;

import com.shemuel.timeline.common.RestResult;
import com.shemuel.timeline.config.S3Service;
import com.shemuel.timeline.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/file/")
@Slf4j
public class FileController {

    @Autowired
    private S3Service s3Service;
    
    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public RestResult<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return RestResult.error("上传失败，请选择文件");
            }
            
            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            // 获取文件后缀
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 生成新的文件名
            String fileName = UUID.randomUUID().toString() + suffix;
            // 生成文件存储路径（按日期分类）
            String filePath = DateUtil.datePath() + "/" + fileName;
            
            // 直接从InputStream上传到S3，避免创建临时文件
            String fileUrl = s3Service.uploadFileWithKey(filePath, file.getInputStream());

            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("originalFilename", originalFilename);
            result.put("fileUrl", fileUrl);
            result.put("thumbnailUrl", fileUrl);
            result.put("filePath", filePath);
            
            return RestResult.success(result);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return RestResult.error("文件上传失败：" + e.getMessage());
        }
    }


    /**
     * 获取文件信息
     */
    @GetMapping("/info")
    public RestResult<Map<String, Object>> getFileInfo(@RequestParam("filePath") String filePath) {
        try {
            // 获取文件URL
            String fileUrl = s3Service.getFileSignedUrl(filePath, 3600000);
            
            // 获取缩略图URL（如果是图片类型）
            String thumbnailUrl = null;
            if (isImageFile(filePath)) {
                thumbnailUrl = s3Service.getThumbnailSignedUrl(filePath, 1000000);
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("fileUrl", fileUrl);
            result.put("thumbnailUrl", thumbnailUrl);
            result.put("filePath", filePath);
            
            return RestResult.success(result);
        } catch (Exception e) {
            log.error("获取文件信息失败", e);
            return RestResult.error("获取文件信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String fileName) {
        String suffix = fileName.toLowerCase();
        return suffix.endsWith(".jpg") || suffix.endsWith(".jpeg") || 
               suffix.endsWith(".png") || suffix.endsWith(".gif") || 
               suffix.endsWith(".bmp") || suffix.endsWith(".webp");
    }
}
