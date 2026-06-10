package com.example.XSJ.Pojo.Ai;

import lombok.Data;
import java.time.LocalTime;

@Data
public class UserAndAi {
    private String userAndAi;      // 对话记录唯一标识（主键）
    private String userId;          // 用户ID
    private String convTitle;       // 对话标题
    private LocalTime creatTime;    // 创建时间
    private LocalTime updateTime;   // 更新时间
    private Integer isDeleted;      // 删除状态（1=未删除，0=已删除）
    private String aiId;            // AI ID
}