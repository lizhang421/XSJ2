package com.example.XSJ.Pojo.VideoDTo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoResolutionDTO {
    private String id;          // 主键 R_开头
    private String videoId;     // 关联video表的VideoId
    private String resolution;  // 分辨率 480P/720P/1080P
    private String videoUrl;    // 该清晰度视频地址
    private Integer width;      // 视频宽度
    private Integer height;     // 视频高度
    private Long bitrate;       // 码率
    private Long size;          // 文件大小字节
    private String format;      // 视频格式 mp4
    private Long duration;
    private LocalDateTime createTime; // 创建时间（数据库自动赋值）
}