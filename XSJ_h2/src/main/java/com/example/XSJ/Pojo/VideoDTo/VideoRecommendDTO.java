package com.example.XSJ.Pojo.VideoDTo;

import lombok.Data;
import java.time.LocalDateTime;


@Data
public class VideoRecommendDTO {
    // ========== 视频核心展示字段 ==========
    private String videoId;          // 视频ID
    private String enCode;           // 视频编码
    private String videoName;        // 视频名称
    private String videoImageUrl;    // 视频封面
    private String videoUrl;         // 视频播放地址
    private Integer videoTime;       // 视频时长（秒）
    private Integer viewCount;       // 播放量
    private Integer likeCount;       // 点赞数
    private Integer commentCount;    // 评论数
    private String videoResolution;  // 视频分辨率
    private Boolean isMember;        // 是否会员视频
    private Integer isCollection;    // 是否属于集合（1=是，0=否）
    private Integer videoSort;

    // ========== 集合信息（仅isCollection=1时返回） ==========
    private VideoCollectionSimpleDTO collectionInfo;

    /**
     * 集合精简DTO（去除敏感字段：createUser/updateUser）
     */
    @Data
    public static class VideoCollectionSimpleDTO {
        private String collectionId;     // 集合ID
        private String collectionName;   // 集合名称
        private String collectionCoverUrl;// 集合封面
        private String collectionDesc;   // 集合描述
        private Integer collectionStatus;// 集合状态
        private LocalDateTime createTime;// 集合创建时间（非敏感，可保留）
        // 去除：updateTime/createUser/updateUser（敏感/前端无需）
    }

    // ========== 以下为后端内部字段，前端无需，直接剔除 ==========
    // 剔除：PublisherId/ShareCount/CollectCount/IsFeature/VideoSort/IsDelete/VideoStatus/CreateTime
    // 剔除：集合敏感字段：updateTime/createUser/updateUser
}