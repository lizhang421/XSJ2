package com.example.XSJ.Pojo.MV;

import lombok.Data;

/**
 * 用户大屏统计数据DTO
 */
@Data
public class UserDashboardStatsDTO {
    /**
     * 总用户数
     */
    private Integer totalUsers;

    /**
     * 用户增长率(%)
     */
    private Double userGrowth;

    /**
     * 今日新增用户
     */
    private Integer todayUsers;

    /**
     * 今日用户增长率(%)
     */
    private Double todayUserGrowth;
}