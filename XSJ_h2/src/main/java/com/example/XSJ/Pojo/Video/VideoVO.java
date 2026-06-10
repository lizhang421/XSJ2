package com.example.XSJ.Pojo.Video;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class VideoVO {


    private String enCode;          // 视频ID
    private String videoName;        // 视频名称
    private String videoImageUrl;    // 视频封面地址
    private String videoUrl;         // 视频播放地址
    private String publisherName;    // 发布人名称（替换publisherId）
    private Integer videoTime;       // 视频时长（秒）
    private Integer viewCount;       // 播放量
    private Integer likeCount;       // 点赞数
    private Integer commentCount;     // 评论数
    private Integer shareCount;      //分享数
    private Integer collectCount;    // 收藏数
    private Integer  isFeature;      //是否精选
    private String videoResolution;  // 视频分辨率
    private Boolean isMember;        // 是否会员视频（0/1转布尔）
    private Boolean isCollection;  // 是否属于集合
    private  String introduction;
    private int videoStatus;

}
