package com.example.XSJ.Pojo.VideoDTo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

    @Data
    public class UserVideoHistoryDTO {
        private String videoId;

        private String enCode;
        private String videoName;
        private String videoImageUrl;
        private String videoUrl;
        private Integer videoTime = 0;
        private Integer viewCount = 0;
        private Integer likeCount = 0;
        private Integer commentCount = 0;
        private Integer shareCount = 0;
        private Integer collectCount = 0;
        private Integer isFeature = 0;
        private String videoResolution;
        private Integer isCollection = 0;
        private Integer videoSort = 0;
        private Integer isDelete = 0;
        private Integer videoStatus = 0;
        private LocalDateTime createTime;
        private String introduction;
        private String publisherName;
        private String watchTime;
        private int duration;

        // 一对多类型
        private List<String> typeNames;

    }



