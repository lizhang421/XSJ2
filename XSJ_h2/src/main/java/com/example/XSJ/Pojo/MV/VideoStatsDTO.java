package com.example.XSJ.Pojo.MV;

import lombok.Data;

@Data
public class VideoStatsDTO {
    private Integer totalVideos;
    private Integer passedVideos;
    private Integer pendingVideos;
    private Long totalViews;
    private Double passRate;
    private Double pendingRate;
}