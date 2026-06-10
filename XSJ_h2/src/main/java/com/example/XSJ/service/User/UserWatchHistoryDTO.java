package com.example.XSJ.service.User;

import lombok.Data;
import java.sql.Timestamp;


@Data
public class UserWatchHistoryDTO {

    // 用户ID
    private String userId;

    // 视频ID
    private String videoId;

    // 视频类型ID
    private String videoTypeId;

    // 类型名称
    private String typeName;

    // 观看历史表的唯一ID（用来去重）
    private String id;

    // 观看时间
    private Timestamp watchTime;

    // 观看时长
    private int duration;
}