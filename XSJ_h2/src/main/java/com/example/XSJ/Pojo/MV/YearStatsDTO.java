package com.example.XSJ.Pojo.MV;

import lombok.Data;

import java.util.List;

@Data
public class YearStatsDTO {
    // 12个月
    private List<String> months;
    // 月用户增长
    private List<Integer> userGrowth;
    // 月视频上传量
    private List<Integer> videoUpload;
    // 月播放量
    private List<Integer> viewCount;
}