package com.example.XSJ.Pojo.middle;

import lombok.Data;
import java.util.Date;

/**
 * 视频与池关联表
 * 数据库表名：VideoAndPoolCofig
 */
@Data
public class VideoAndPoolCofig {
    /**
     * 视频与池关联表主键
     * 数据库字段：VideoAndPoolId
     */
    private String videoAndPoolId;

    /**
     * 视频Id
     * 数据库字段：VideoId
     */
    private String videoId;

    /**
     * 池Id
     * 数据库字段：PoolId
     */
    private String poolId;

    /**
     * 入池时间
     * 数据库字段：JoinPoolTime
     */
    private Date joinPoolTime;

    /**
     * 出池时间
     * 数据库字段：LeavePoolTime
     */
    private Date leavePoolTime;

    /**
     * 入池原因
     * 数据库字段：JoinReason
     */
    private String joinReason;

    /**
     * 出池原因
     * 数据库字段：LeaveReason
     */
    private String leaveReason;

    //是否生效
    private int Status;
}