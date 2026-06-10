package com.example.XSJ.Pojo.VideoDTo;

import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 用户视频浏览lishi 记录请求DTO
 *
 * @author xsj
 * @date 2021-05-07 15:05:05
 */
@Data
public class UserViewHistoryDTO {
    /**
     * 视频代码
     * 数据库字段：ENcode
     */
    private String enCode;

    /**
     * 观看时间
     * 数据库字段：watchTime
     */
    private String watchTime;

    /**
     * 本次观看时长百分比
     * 数据库字段：duration
     */
    private int duration;

}
