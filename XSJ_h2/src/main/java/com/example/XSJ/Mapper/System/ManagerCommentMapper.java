package com.example.XSJ.Mapper.System;

import com.example.XSJ.Pojo.Video.VideoCommentManager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ManagerCommentMapper {

    @Select("<script>"
            + "SELECT "
            + "  vc.videoCommentId, "
            + "  vc.parentId, "
            + "  vc.content, "
            + "  vc.likeCount, "
            + "  vc.status, "
            + "  vc.createTime, "
            + "  vc.isRead, "
            + "  u.nickName AS userNickName, "
            + "  u.avatar AS userAvatar, "
            + "  u.enCode AS userEncode, "
            + "  vc_parent.content AS originalContent, "
            + "  v.videoName, "
            + "  v.videoImageUrl, "
            + "  v.enCode AS videoEncode "
            + "FROM videocomment vc "
            + "LEFT JOIN video v ON v.VideoId = vc.VideoId "
            + "LEFT JOIN user u ON u.UserId = vc.userId "
            + "LEFT JOIN videocomment vc_parent ON vc.parentId = vc_parent.videoCommentId "
            + "WHERE 1=1 "
            + "<if test='status != null'> AND vc.status = #{status} </if>"
            + "<if test='startTime != null'> AND vc.createTime >= #{startTime} </if>"
            + "<if test='endTime != null'> AND vc.createTime &lt;= #{endTime} </if>"
            + "<if test='key != null and key != \"\"'>"
            + "   AND (vc.content LIKE CONCAT('%', #{key}, '%') OR vc.videoCommentId = #{key} OR u.enCode = #{key}) "
            + "</if>"
            + "ORDER BY vc.createTime DESC "
            + "LIMIT #{offset}, #{pageSize}"
            + "</script>")
    List<VideoCommentManager> getCommentList(
            @Param("offset") int offset,
            @Param("pageSize") int pageSize,
            @Param("status") Integer status,
            @Param("key") String key,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Select("<script>"
            + "SELECT COUNT(*) FROM videocomment vc "
            + "LEFT JOIN video v ON v.VideoId = vc.VideoId "
            + "LEFT JOIN user u ON u.UserId = vc.userId "
            + "WHERE 1=1 "
            + "<if test='status != null'> AND vc.status = #{status} </if>"
            + "<if test='startTime != null'> AND vc.createTime >= #{startTime} </if>"
            + "<if test='endTime != null'> AND vc.createTime &lt;= #{endTime} </if>"
            + "<if test='key != null and key != \"\"'>"
            + "   AND (vc.content LIKE CONCAT('%', #{key}, '%') OR vc.videoCommentId = #{key} OR u.enCode = #{key}) "
            + "</if>"
            + "</script>")
    int getCommentCount(
            @Param("status") Integer status,
            @Param("key") String key,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );


    @Update("UPDATE videocomment " +
            "SET status = #{status} " +
            "WHERE videoCommentId = #{commentId}")
    int deleteComment(
            @Param("commentId") String commentId,
            @Param("status") Integer status
    );
}
