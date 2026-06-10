package com.example.XSJ.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.example.XSJ.Pojo.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云OSS上传工具类（从配置文件读取参数）
 */
@Component
public class AliOssUtil {

    // 从配置文件注入参数
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.base-dir:images/}")
    private String baseDir;

    @Value("${aliyun.oss.base-vio:video/}")
    private String basevio;

    /**
     * 上传MultipartFile（前端上传的文件）
     * @param file 前端上传的文件
     * @return 上传后的文件访问URL
     */
    public String upload(MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }
        if (file.getSize() > 10 * 1024 * 1024) { // 限制10MB
            throw new RuntimeException("文件大小不能超过10MB");
        }

        OSS ossClient = null;
        try {
            //  生成唯一文件名（避免重复）
            String originalFileName = file.getOriginalFilename();
            String fileSuffix = getFileSuffix(originalFileName);
            String fileName = UUID.randomUUID().toString() + fileSuffix;
            String ossFilePath = baseDir + fileName;

            //  获取文件流
            InputStream inputStream = file.getInputStream();
            // 设置文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getContentType(fileSuffix)); // 设置图片类型
            metadata.setContentLength(inputStream.available()); // 设置文件大小

            // 创建OSS客户端并上传
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ossClient.putObject(bucketName, ossFilePath, inputStream, metadata);

            String endpointHost = endpoint.replace("https://", "");
            return "https://" + bucketName + "." + endpointHost + "/" + ossFilePath;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        } finally {
            //  关闭OSS客户端
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    //视频文件上传方法
    public String uploadVideo(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传视频文件不能为空");
        }
        if (file.getSize() > 10L * 1024 * 1024 * 1024) {
            throw new RuntimeException("上传文件超过限制");
        }
        OSSClient ossClient = null;
        try {
            //设置一个唯一的名字
            String originalFileName = file.getOriginalFilename();
            String fileSuffix = getFileSuffix(originalFileName);   //获取文件后缀名
            String fileName = UUID.randomUUID().toString() + fileSuffix;  //生成唯一的文件名称
            String ossFilePath = basevio + fileName; // 视频目录
            //获取文件传输流
            InputStream inputStream = file.getInputStream();
            //设置文件属性 封装文件的基础属性
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getContentType(fileSuffix));
            metadata.setContentLength(inputStream.available());

            ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

            ossClient.putObject(bucketName, ossFilePath, inputStream, metadata);

            String endpointHost = endpoint.replace("https://", "");

            return "https://" + bucketName + "." + endpointHost + "/" + ossFilePath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    // ===================== 【我只给你加这一个方法！】 =====================
    /**
     * 上传 本地File文件（给转码后视频用）
     */
    public String uploadLocalVideo(File file) {
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        OSSClient ossClient = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            String suffix = getFileSuffix(file.getName());
            String fileName = UUID.randomUUID().toString() + suffix;
            String ossFilePath = basevio + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("video/mp4");
            metadata.setContentLength(file.length());

            ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            ossClient.putObject(bucketName, ossFilePath, fis, metadata);

            String endpointHost = endpoint.replace("https://", "");
            return "https://" + bucketName + "." + endpointHost + "/" + ossFilePath;
        } catch (Exception e) {
            throw new RuntimeException("本地视频上传失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 上传本地文件（可选）
     * @param localFilePath 本地文件路径（如D:/test.jpg）
     * @return 访问URL
     */
    public String uploadLocalFile(String localFilePath) {
        return "";
    }


    /**
     * 获取文件后缀（如 .jpg）
     */
    private String getFileSuffix(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".jpg"; // 默认后缀（避免无后缀）
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 设置Content-Type（关键：让浏览器预览而非下载）
     */
    private String getContentType(String suffix) {
        suffix = suffix.toLowerCase();
        return switch (suffix) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".bmp" -> "image/bmp";
            case ".webp" -> "image/webp";
            case ".mp4" -> "video/mp4";
            default -> "application/octet-stream";
        };
    }


}