package com.example.XSJ.Pojo.VideoDTo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class VideoTagDTO {
    /**
     * 视频基础信息（原有字段）
     */

    private String enCode;
    private String videoName;
    private String videoImageUrl;
    private String videoUrl;

    //这个字段不是从video中获取的是要外键到用户表去获取的
    private String publisherName;
    private Integer videoTime = 0;
    private Integer viewCount = 0;
    private Integer likeCount = 0;
    private Integer commentCount = 0;
    private Integer shareCount = 0;
    private Integer collectCount = 0;
    private Integer isFeature = 0;
    private String videoResolution;
    private Integer isCollection = 0; // 1-属于集合，0-不属于
    private Integer videoSort = 0;
    private Integer isDelete = 0;
    private Integer videoStatus = 0;
    private LocalDateTime createTime;
    private String introduction;
    private VideoCollectionDTO collectionInfo;
}
