package com.example.XSJ.Mapper.System;

import com.example.XSJ.Pojo.Video.VideoVO;
import com.example.XSJ.Pojo.VideoDTo.VideoPendingDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ManagerVideoMapper {


    @Select("SELECT " +
            "v.enCode, " +
            "v.videoName, " +
            "v.videoImageUrl, " +
            "v.videoUrl, " +
            "u.nickName AS publisherName, " +
            "v.videoTime, " +
            "v.viewCount, " +
            "v.likeCount, " +
            "v.commentCount, " +
            "v.shareCount, " +
            "v.collectCount, " +
            "v.isFeature, " +
            "v.videoResolution, " +
            "v.isMember, " +
            "v.isCollection, " +
            "v.introduction, " +
            "v.videoStatus " +  // 这里已加入视频状态字段
            "FROM video v " +
            "LEFT JOIN user u ON v.publisherId = u.enCode " +
            "ORDER BY v.createTime DESC " +
            "LIMIT #{start}, #{pageSize}")
    List<VideoVO> getPostVideo(@Param("pageSize") int pageSize, @Param("start") int start);

    @Select("SELECT " +
            "v.VideoPendingId AS videoPendingId, " +
            "v.EnCode AS enCode, " +
            "v.VideoName AS videoName, " +
            "v.VideoDesc AS videoDesc, " +
            "v.VideoImageUrl AS videoImageUrl, " +
            "v.VideoUrl AS videoUrl, " +
            "v.PublisherId AS publisherId, " +
            "v.PublishCode AS publishCode, " +
            "v.ReviewStatus AS reviewStatus, " +
            "v.PublishTime AS publishTime, " +
            "v.VideoTime AS videoTime, " +
            "v.VideoSize AS videoSize, " +
            "v.VideoFormat AS videoFormat, " +
            "v.IsCollection AS isCollection, " +
            "v.CollectionId AS collectionId, " +
            "v.VideoStatus AS videoStatus, " +
            "v.AuditTime AS auditTime, " +
            "v.EndAuditTime AS endAuditTime, " +
            "v.IsDelete AS isDelete, " +
            "v.ReviewerCode AS reviewerCode, " +
            "v.ReviewerId AS reviewerId, " +
            "v.CreateTime AS createTime, " +
            "v.UpdateTime AS updateTime " +
            "FROM videopending v " +
            "WHERE v.VideoStatus != 1 " +  // 这里加上过滤！
            "ORDER BY v.CreateTime DESC " +
            "LIMIT #{start}, #{pageSize}")
    List<VideoPendingDTO> getVideoPendingList(
            @Param("pageSize") int pageSize,
            @Param("start") int start
    );


    @Select("SELECT COUNT(*) FROM video v " +
            "LEFT JOIN user u ON v.publisherId = u.enCode " +
            "ORDER BY v.createTime DESC")
    int getPostVideoCount();

    @Select("SELECT COUNT(*) FROM videopending v WHERE v.VideoStatus != 1")
    int getPostVideoPendingCount();
}
