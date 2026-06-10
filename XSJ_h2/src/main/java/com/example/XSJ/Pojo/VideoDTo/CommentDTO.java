package com.example.XSJ.Pojo.VideoDTo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
public class CommentDTO {



    // 新增父评论人头像字段
    private String parentCommentAvatar;
    /**
     * 头像
     */
    private String avatar;


    /**
     * 评论ID（主键）
     * 数据库字段：VideoCommentId
     */
    private String videoCommentId;

    /**
     * 子评论列表（二级嵌套核心）
     * 根评论：存储它的所有子评论；子评论：该字段为空（二级评论无需再嵌套）
     * 【修改2】初始化空列表，避免前端空指针
     */
    private List<CommentDTO> replyList = new ArrayList<>();

    /**
     * 评论发布者ID
     * 数据库字段：UserId
     */
    private String userId;

    /**
     * 【新增】当前评论发布者昵称（前端展示评论人名称必备）
     */
    private String commentNickname;

    /**
     * 父评论ID，空字符串表示根评论（直接评论视频）
     * 数据库字段：ParentId
     */
    private String parentId = "";

    /**
     * 父评论发布者昵称
     * 父评论为空则不返回
     */
    private String parentCommentNickname;

    /**
     * 评论内容（扩容到2000字符，满足长评论需求）
     * 数据库字段：Content
     */
    private String content;

    /**
     * 点赞数，默认0
     * 数据库字段：LikeCount
     */
    private Integer likeCount = 0;

    /**
     * 评论状态：1-正常，2-屏蔽，3-删除
     * 数据库字段：Status
     */
    private Integer status = 1;

    /**
     * 评论创建时间
     * 数据库字段：CreateTime
     */
    private LocalDateTime createTime;

}

