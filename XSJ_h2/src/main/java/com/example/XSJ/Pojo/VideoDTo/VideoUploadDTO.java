package com.example.XSJ.Pojo.VideoDTo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 前端传递的json格式数据
 */

@Data
public class VideoUploadDTO {

    private String videoName;    // 小驼峰
    private String videoDesc;
    private String videoUrl;
    private String videoImageUrl;
    private Integer isCollection;
    private String collectionId;
    private String videoTypeId;
}