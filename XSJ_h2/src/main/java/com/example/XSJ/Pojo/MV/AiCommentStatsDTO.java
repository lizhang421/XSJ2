package com.example.XSJ.Pojo.MV;

import lombok.Data;

@Data
public class AiCommentStatsDTO {
    /** AI模型数量 */
    private Integer modelCount;
    /** 评论总数 */
    private Integer commentCount;
    private Integer totalFileCount;
}