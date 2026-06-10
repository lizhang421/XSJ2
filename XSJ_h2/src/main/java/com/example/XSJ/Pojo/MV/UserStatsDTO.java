package com.example.XSJ.Pojo.MV;

import lombok.Data;

@Data
public class UserStatsDTO {
    private Integer totalUsers;
    private Double userGrowth;
    private Integer todayUsers;
    private Double todayUserGrowth;
}