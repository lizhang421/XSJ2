package com.example.XSJ.Pojo.MV;

import lombok.Data;

@Data
public class VideoTypeStatsDTO {
    private String name;   // 分类名称
    private Integer count;  // 数量
    private Integer percent; // 占比(%)
}