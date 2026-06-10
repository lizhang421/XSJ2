package com.example.XSJ.Pojo.VideoDTo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 视频互动操作请求DTO（点赞/收藏/转发）
 */
@Data
public class VideoInteractReqDTO {
    /**
     * 视频ID（前端必传）
     */
    @JsonProperty("EnCode")
    @NotNull(message = "视频EnCode不能为空")
    private String EnCode;

    /**
     * 操作类型（like-点赞 collect-收藏 share-转发 comment-评论）
     * 前端必传，限定枚举值
     */
    @NotNull(message = "操作类型不能为空")
    private String operateType;

    /**
     * 操作状态（1-操作 0-取消操作）
     * 前端必传，仅0/1
     */
    @NotNull(message = "操作状态不能为空")
    private Integer status;

}