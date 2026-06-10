package com.example.XSJ.Pojo.MV;

import lombok.Data;

import java.util.List;

@Data
public class DashboardResponseDTO {
    // 用户统计
    private UserStatsDTO userData;
    // 视频统计
    private VideoStatsDTO videoData;
    // 近7天用户增长数量（数组格式）
    private List<Integer> userWeekGrowthData;
    // 全年月度统计（新增！）
    private YearStatsDTO yearStatsData;

    // 学历分布
    private List<PieItemDTO> educationData;
    // 语言分布
    private List<PieItemDTO> languageData;
    // 视频类型分布
    private List<VideoTypeStatsDTO> videoTypeData;
    // AI&评论统计
    private AiCommentStatsDTO aiCommentData;
    private List<PieItemDTO> aiUsageData;


}