package com.example.XSJ.Pojo.User;

import lombok.Data;

@Data
public class UserVideoInteractDTO {
    private String userId;
    private String videoId;
    private Integer isLike;
    private Integer isCollect;
    private Integer isComment;
    private Integer isShare;
    private Integer playPercent;  // 播放百分比
    private Integer interactScore; // 数据库里已经算好的互动总分
}