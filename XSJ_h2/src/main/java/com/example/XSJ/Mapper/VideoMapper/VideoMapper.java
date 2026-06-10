package com.example.XSJ.Mapper.VideoMapper;

import com.example.XSJ.Pojo.PoolExtractRule;
import com.example.XSJ.Pojo.System.UserReview;
import com.example.XSJ.Pojo.User.AccountStatus;
import com.example.XSJ.Pojo.UserDTO.PoolVideoDTO;
import com.example.XSJ.Pojo.UserDTO.UserMessageDTO;
import com.example.XSJ.Pojo.UserDTO.Userandvideinterseat;
import com.example.XSJ.Pojo.Video.*;

import com.example.XSJ.Pojo.Video.VideoResolutionDTO;
import com.example.XSJ.Pojo.VideoDTo.*;
import org.apache.ibatis.annotations.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface VideoMapper {
    @Select("SELECT COUNT(*) FROM video WHERE VideoId = #{videoId} AND EnCode = #{videoEnCode}")
    int getdate(@Param("videoId") String videoId, @Param("videoEnCode") String videoEnCode);


    @Select("SELECT UserTagWeight from user WHERE EnCode=#{code} ")
    String getdatebycode(String code);

    /**
     * PoolVideoDTO
     * 需要返回PoolVideoDTO 类型的数据
     *
     * @param typeList 所需要查询的池的种类
     * @return PoolVideoDTO {
     * /**
     * * 所属池id
     *
     */
    @Select({
            "<script>",
            "SELECT ",
            "vp.PoolId, vp.TrafficRatio, vp.PoolFullName, vp.PoolType, vp.PoolCategory,vp.GetPoolMount ",
            "FROM video v ",
            "WHERE vp.PoolStatus=1" +
                    "AND",
            "vp.PoolType IN (",
            "<foreach collection='typeList' item='typeItem' separator=','>",
            "#{typeItem.typeCode}",
            "</foreach>",
            ")",
            "</script>"
    })
    List<PoolVideoDTO> getpoolvideobytope(@Param("typeList") List<Type> typeList);


    @Select({"SELECT v.* FROM video v " +
            "LEFT JOIN videoandpoolcofig vc ON v.VideoId = vc.VideoId " +
            "WHERE vc.PoolId = #{poolId} " +
            "  AND v.ReviewStatus = 1 " +
            "  AND v.IsDelete = 0 " +
            "  AND v.VideoStatus = 0"})
    List<Video> getvideolistbypool(@Param("poolId") String poolId);

    @Select("select * from videopoolconfig where VideoPoolConfigId=#{poolId}")
    VideoPoolConfig getdatebypoolId(String poolId);

    @Select("select VideoPoolConfigId FROM videopoolconfig " +
            "where PoolCaetGory LIKE CONCAT('%', #{standardCategory}, '%') " +
            "OR PoolFullName LIKE CONCAT('%', #{standardCategory}, '%')")
    List<String> findpoolidbytype(String standardCategory);

    @Select("SELECT video.* FROM video " +
            "LEFT JOIN videoandpoolcofig ON videoandpoolcofig.VideoId = video.VideoId " +
            "WHERE videoandpoolcofig.PoolId = #{poolId} " +
            "  AND video.ReviewStatus = 1 " +
            "  AND video.IsDelete = 0 " +
            "  AND video.VideoStatus = 0")
    List<Video> findpoolvideobypoolid(@Param("poolId") String poolId);


    @Select({
            "<script>",
            "SELECT * FROM video",
            "<where>",
            "  ReviewStatus = 1",
            "  AND IsDelete = 0",
            "  AND VideoStatus = 0",
            "  <if test='videoIds != null and videoIds.size() > 0'>",
            "    AND videoId NOT IN",
            "    <foreach collection='videoIds' item='id' open='(' separator=',' close=')'>",
            "      #{id}",
            "    </foreach>",
            "  </if>",
            "</where>",
            " ORDER BY RAND()",
            " LIMIT #{mount}",
            "</script>"
    })
    List<Video> getRandomVideo(
            @Param("mount") int mount,
            @Param("videoIds") List<String> videoIds
    );


    @Select("SELECT * from poolextractrule WHERE pool_type=#{poolType} AND category='*' AND layer_ratio_check=1 AND extract_ratio_check=1 AND enable=1")
    PoolExtractRule getPoolVideoConfig(String poolType);

    /**
     * 根据视频ID查询未删除、公开的视频配套资源
     *
     * @param
     * @return 视频资源列表
     */
    @Select("""
            SELECT vr.VideoResourcesId,vr.ResourceName, vr.ResourceFormat, vr.ResourceUrl, 
                   vr.ResourceThumUrl, vr.ResourceSize, vr.DownloadCount, vr.ResourceDesc
            FROM videoresources vr
            LEFT JOIN video v ON vr.VideoId = v.VideoId
            WHERE (v.EnCode = #{enCode} OR v.EnCode IS NULL)
              AND vr.IsDelete = 0
              AND vr.IsPublic = 1
            """)
    List<VideoFileDTO> findVideoResourcesByCode(@Param("enCode") String enCode);


    @Select("""
            SELECT 
                -- 主评论字段
                vc.VideoCommentId AS videoCommentId,
                vc.UserId AS userId,
                vc.ParentId AS parentId,
                vc.Content AS content,
                vc.LikeCount AS likeCount,
                vc.Status AS status,
                vc.CreateTime AS createTime,
                -- 当前评论人信息
                u.NickName AS commentNickname,
                u.Avatar AS avatar
            FROM video v
            -- 关联评论表（只查有效评论：status=1）
            JOIN videocomment vc ON v.VideoId = vc.VideoId AND vc.Status = 1  -- 改为JOIN，排除无评论的行
            -- 关联当前评论人的用户表
            LEFT JOIN user u ON vc.UserId = u.UserId
            -- 关联父评论表（通过parentId）
            LEFT JOIN videocomment pvc ON vc.ParentId = pvc.VideoCommentId
            -- 关联父评论人的用户表
            LEFT JOIN user pu ON pvc.UserId = pu.UserId
            -- 筛选条件：视频encode匹配
            WHERE v.Encode = #{encode}
            -- 排序：按创建时间降序
            ORDER BY vc.CreateTime DESC
            """)
    @Results({
            @Result(id = true, column = "videoCommentId", property = "videoCommentId"),
            @Result(column = "userId", property = "userId"),
            @Result(column = "parentId", property = "parentId"),
            @Result(column = "content", property = "content"),
            @Result(column = "likeCount", property = "likeCount"),
            @Result(column = "status", property = "status"),
            @Result(column = "createTime", property = "createTime"),
            @Result(column = "commentNickname", property = "commentNickname"),
            @Result(column = "avatar", property = "avatar")
    })
    List<CommentDTO> findVideoCommentsByCode(String encode);


    /**
     * 修复后：SQL字段名 → @Results.column → DTO.property 完全一致
     */
    @Select("""
            SELECT 
                vf.ResourceName,        -- 对应DTO的resourceName
                vf.ResourceFormat,      -- 对应DTO的resourceFormat
                vf.ResourceUrl,         -- 对应DTO的resourceUrl
                vf.ResourceThumUrl,     -- 补充：DTO有这个属性，SQL必须查
                vf.ResourceSize,        -- 对应DTO的resourceSize
                vf.DownloadCount,       -- 补充：DTO有这个属性，SQL必须查
                vf.ResourceDesc         -- 对应DTO的resourceDesc
            FROM video v
            LEFT JOIN videoresources vf ON v.VideoId = vf.VideoId
            WHERE v.Encode = #{encode}
              AND vf.IsDelete = 0  
              AND vf.IsPublic = 1  
            ORDER BY vf.UploadTime DESC
            """)
    @Results({
            @Result(column = "ResourceName", property = "resourceName"),
            @Result(column = "ResourceFormat", property = "resourceFormat"),
            @Result(column = "ResourceUrl", property = "resourceUrl"),
            @Result(column = "ResourceThumUrl", property = "resourceThumUrl"),
            @Result(column = "ResourceSize", property = "resourceSize"),
            @Result(column = "DownloadCount", property = "downloadCount"),
            @Result(column = "ResourceDesc", property = "resourceDesc")
    })
    List<VideoFileDTO> findVideoFileByCode(String encode);

    @Select("""
            SELECT
                u.EnCode,
                u.NickName,
                u.Avatar,
                u.UiMember,
                u.Country,
                u.BackImage,
                u.School
            FROM `user` u
            LEFT JOIN video v on v.PublisherId = u.UserId
            WHERE v.EnCode = #{encode}
            """)
    @Results({
            // 数据库字段 → DTO字段映射（解决大小写/驼峰不一致）
            @Result(column = "NickName", property = "nickName"),
            @Result(column = "Avatar", property = "avatar"),
            @Result(column = "UiMember", property = "uiMember"),
            @Result(column = "Country", property = "country"),
            @Result(column = "School", property = "school")
    })
    List<UserResourcesDTO> findUserResourcesByCode(String encode);

    @Select("""
            WITH target_video AS (
                SELECT 
                    CollectionId,
                    PublisherId
                FROM video
                WHERE EnCode = #{encode}
                  AND IsDelete = 0
                  AND VideoStatus = 0
            ),
            collection_videos AS (
                SELECT 
                    v.VideoId,
                    v.EnCode,
                    v.VideoName,
                    v.VideoImageUrl,
                    v.VideoUrl,
                    v.PublisherId,
                    v.VideoTime,
                    v.ViewCount,
                    v.LikeCount,
                    v.CommentCount,
                    v.ShareCount,
                    v.CollectCount,
                    v.IsFeature,
                    v.VideoResolution,
                    v.IsMember,
                    v.IsCollection,
                    v.CollectionId,
                    v.VideoSort
                FROM video v
                JOIN target_video tv ON v.CollectionId = tv.CollectionId
                WHERE v.IsDelete = 0
                  AND v.VideoStatus = 0
                  AND tv.CollectionId IS NOT NULL
                ORDER BY v.VideoSort ASC
            ),
            publisher_videos AS (
                SELECT 
                    v.VideoId,
                    v.EnCode,
                    v.VideoName,
                    v.VideoImageUrl,
                    v.VideoUrl,
                    v.PublisherId,
                    v.VideoTime,
                    v.ViewCount,
                    v.LikeCount,
                    v.CommentCount,
                    v.ShareCount,
                    v.CollectCount,
                    v.IsFeature,
                    v.VideoResolution,
                    v.IsMember,
                    v.IsCollection,
                    v.CollectionId,
                    v.VideoSort
                FROM video v
                JOIN target_video tv ON v.PublisherId = tv.PublisherId
                WHERE v.IsDelete = 0
                  AND v.VideoStatus = 0
                  AND tv.CollectionId IS NULL
                ORDER BY v.CreateTime DESC
            )
            SELECT * FROM collection_videos
            UNION ALL
            SELECT * FROM publisher_videos
            """)
    List<VideoTagDTO> findRecommendVideosByEncode(String encode);

    /**
     * 查询推荐视频（精简版：新增VideoSort返回，修复SQL语法错误）
     */
    @Select("""
            WITH target_video AS (
                -- 仅查询关联所需的核心字段
                SELECT 
                    v.CollectionId,
                    v.PublisherId,
                    v.IsCollection
                FROM video v
                WHERE v.EnCode = #{encode}
                  AND v.IsDelete = 0
                  AND v.VideoStatus = 0
            ),
            collection_info AS (
                -- 集合仅查前端所需字段，剔除createUser/updateUser
                SELECT 
                    g.groupId,
                    g.groupName,
                    g.groupCoverUrl,
                    g.groupDesc,
                    g.groupStatus,
                    g.createTime
                FROM videogroup g
                JOIN target_video tv ON g.groupId = tv.CollectionId
                WHERE g.groupStatus = 0
            ),
            collection_videos AS (
                -- 视频仅查前端所需字段，剔除敏感/无用字段（修复逗号缺失+保留VideoSort）
                SELECT 
                    v.VideoId,
                    v.EnCode,
                    v.VideoName,
                    v.VideoImageUrl,
                    v.VideoUrl,
                    v.VideoTime,
                    v.ViewCount,
                    v.LikeCount,
                    v.CommentCount,
                    v.VideoResolution,
                    v.IsMember,
                    v.IsCollection,
                    v.VideoSort,  -- 保留视频排序字段
                    -- 集合关联字段（修复：新增逗号，否则SQL语法错误）
                    ci.groupId,
                    ci.groupName,
                    ci.groupCoverUrl,
                    ci.groupDesc,
                    ci.groupStatus,
                    ci.createTime
                FROM video v
                JOIN target_video tv ON v.CollectionId = tv.CollectionId
                LEFT JOIN collection_info ci ON v.CollectionId = ci.groupId
                WHERE v.IsDelete = 0
                  AND v.VideoStatus = 0
                  AND tv.IsCollection = 1
                ORDER BY v.VideoSort ASC
            ),
            publisher_videos AS (
                -- 同发布者视频：仅查前端所需字段，集合信息为NULL（补充VideoSort）
                SELECT 
                    v.VideoId,
                    v.EnCode,
                    v.VideoName,
                    v.VideoImageUrl,
                    v.VideoUrl,
                    v.VideoTime,
                    v.ViewCount,
                    v.LikeCount,
                    v.CommentCount,
                    v.VideoResolution,
                    v.IsMember,
                    v.IsCollection,
                    v.VideoSort,  -- 补充：同发布者视频也返回排序字段
                    -- 无集合时为NULL
                    NULL AS groupId,
                    NULL AS groupName,
                    NULL AS groupCoverUrl,
                    NULL AS groupDesc,
                    NULL AS groupStatus,
                    NULL AS createTime
                FROM video v
                JOIN target_video tv ON v.PublisherId = tv.PublisherId
                WHERE v.IsDelete = 0
                  AND v.VideoStatus = 0
                  AND tv.IsCollection = 0
                ORDER BY v.CreateTime DESC
            )
            SELECT * FROM collection_videos
            UNION ALL
            SELECT * FROM publisher_videos
            """)
    @Results({
            // 视频核心字段映射（已包含VideoSort）
            @Result(column = "VideoId", property = "videoId"),
            @Result(column = "EnCode", property = "enCode"),
            @Result(column = "VideoName", property = "videoName"),
            @Result(column = "VideoImageUrl", property = "videoImageUrl"),
            @Result(column = "VideoUrl", property = "videoUrl"),
            @Result(column = "VideoTime", property = "videoTime"),
            @Result(column = "ViewCount", property = "viewCount"),
            @Result(column = "LikeCount", property = "likeCount"),
            @Result(column = "CommentCount", property = "commentCount"),
            @Result(column = "VideoResolution", property = "videoResolution"),
            @Result(column = "IsMember", property = "isMember"),
            @Result(column = "IsCollection", property = "isCollection"),
            @Result(column = "VideoSort", property = "videoSort"),  // 关键：映射排序字段到DTO
            // 集合信息嵌套映射（仅前端所需字段）
            @Result(column = "groupId", property = "collectionInfo.collectionId"),
            @Result(column = "groupName", property = "collectionInfo.collectionName"),
            @Result(column = "groupCoverUrl", property = "collectionInfo.collectionCoverUrl"),
            @Result(column = "groupDesc", property = "collectionInfo.collectionDesc"),
            @Result(column = "groupStatus", property = "collectionInfo.collectionStatus"),
            @Result(column = "createTime", property = "collectionInfo.createTime")
    })
    List<VideoRecommendDTO> findRecommendVideosWithCollection(String encode);


    @Insert("""
            INSERT INTO videocomment (
                VideoCommentId,  -- 自定义主键（UM+32位ID）
                VideoId, 
                UserId, 
                ParentId, 
                Content, 
                LikeCount, 
                Status,          -- videocomment表的状态字段（Status）
                CreateTime
            ) 
            SELECT 
                #{videoCommentId},  -- 传入自定义ID
                -- 核心修改：VideoStatus从1改为0（正常状态）
                (SELECT v.VideoId FROM video v WHERE v.EnCode = #{videoEnCode} AND v.VideoStatus = 0),
                -- 用户状态：如果用户正常状态也是0，就改为u.Status = 0；如果是1则保留
                (SELECT u.UserId FROM user u WHERE u.EnCode = #{userEncode} AND u.Status = 1),
                -- 根评论时parentId设为NULL
                CASE WHEN #{parentId} IS NULL OR #{parentId} = '' OR #{parentId} = '0' THEN NULL ELSE #{parentId} END,
                #{content},
                0,  -- 初始点赞数
                1,  -- 评论状态：有效（按你实际值调整）
                #{createTime}
            WHERE
                -- 核心修改：VideoStatus从1改为0
                EXISTS (SELECT 1 FROM video v WHERE v.EnCode = #{videoEnCode} AND v.VideoStatus = 0)
                -- 用户状态：如果用户正常状态是0则改为u.Status = 0
                AND EXISTS (SELECT 1 FROM user u WHERE u.EnCode = #{userEncode} AND u.Status = 1)
                -- 根评论校验逻辑（无需修改）
                AND (
                    (#{parentId} IS NULL OR #{parentId} = '' OR #{parentId} = '0')
                    OR EXISTS (SELECT 1 FROM videocomment vc WHERE vc.VideoCommentId = #{parentId} AND vc.Status = 1)
                )
            """)
    int InsertIntoComment(
            @Param("videoCommentId") String videoCommentId,
            @Param("videoEnCode") String videoEnCode,
            @Param("userEncode") String userEncode,
            @Param("content") String content,
            @Param("parentId") String parentId,
            @Param("createTime") LocalDateTime createTime
    );



    /**
     * 重构：通过视频编码/用户编码查询互动记录（关联真实ID）
     * @param videoEnCode 视频编码
     * @param userEncode 用户编码
     * @return 整条互动记录（无记录返回null）
     */
    @Select("""
        SELECT 
            vi.id,
            vi.userId,
            vi.videoId,
            vi.isLike,
            vi.isCollect,
            vi.isComment,
            vi.isShare,
            vi.playPercent,
            vi.lastPosition,
            vi.interactScore,
            vi.createTime,
            vi.updateTime
        FROM userVideoInteract vi
        -- 关联用户表：通过编码找真实ID
        INNER JOIN user u ON vi.userId = u.UserId AND u.EnCode = #{userEncode} AND u.Status = 1
        -- 关联视频表：通过编码找真实ID
        INNER JOIN video v ON vi.videoId = v.VideoId AND v.EnCode = #{videoEnCode} AND v.VideoStatus = 0
        LIMIT 1
        """)
    UserVideoInteract checkVideoInteract(
            @Param("videoEnCode") String videoEnCode,
            @Param("userEncode") String userEncode
    );

    @Insert("""
        INSERT INTO userVideoInteract (
            id,
            userId,
            videoId,
            isLike,
            isCollect,
            isComment,
            isShare,
            playPercent,
            lastPosition,
            interactScore,
            createTime,
            updateTime
        ) 
        SELECT 
            #{interact.id},
            (SELECT u.UserId FROM user u WHERE u.EnCode = #{userEncode} AND u.Status = 1),
            (SELECT v.VideoId FROM video v WHERE v.EnCode = #{videoEnCode} AND v.VideoStatus = 0),
            #{interact.isLike},
            #{interact.isCollect},
            #{interact.isComment},
            #{interact.isShare},
            #{interact.playPercent},
            #{interact.lastPosition},
            #{interact.interactScore},
            #{interact.createTime},
            #{interact.updateTime}
        WHERE
            EXISTS (SELECT 1 FROM video v WHERE v.EnCode = #{videoEnCode} AND v.VideoStatus = 0)
            AND EXISTS (SELECT 1 FROM user u WHERE u.EnCode = #{userEncode} AND u.Status = 1)
            AND NOT EXISTS (
                SELECT 1 FROM userVideoInteract vi
                INNER JOIN user u ON vi.userId = u.UserId AND u.EnCode = #{userEncode}
                INNER JOIN video v ON vi.videoId = v.VideoId AND v.EnCode = #{videoEnCode}
            )
        """)
    void insertVideoInteract(
            @Param("interact") UserVideoInteract interact,
            @Param("videoEnCode") String videoEnCode,
            @Param("userEncode") String userEncode
    );

    /**
     * 重构：更新互动记录（通过编码关联真实ID）
     */
    @Update("""
        UPDATE userVideoInteract vi
        INNER JOIN user u ON vi.userId = u.UserId AND u.EnCode = #{userEncode} AND u.Status = 1
        INNER JOIN video v ON vi.videoId = v.VideoId AND v.EnCode = #{videoEnCode} AND v.VideoStatus = 0
        SET 
            vi.isLike = #{interact.isLike},
            vi.isCollect = #{interact.isCollect},
            vi.isComment = #{interact.isComment},
            vi.isShare = #{interact.isShare},
            vi.updateTime = #{interact.updateTime}
        """)
    void updateVideoInteract(
            @Param("interact") UserVideoInteract interact,
            @Param("videoEnCode") String videoEnCode,
            @Param("userEncode") String userEncode
    );

    @Update("<script>" +
            "UPDATE video SET " +
            "<choose>" +
            "   <when test='type == \"like\"'>LikeCount = LikeCount + #{num}</when>" +
            "   <when test='type == \"comment\"'>CommentCount = CommentCount + #{num}</when>" +
            "   <when test='type == \"share\"'>ShareCount = ShareCount + #{num}</when>" +
            "   <when test='type == \"collect\"'>CollectCount = CollectCount + #{num}</when>" +
            "</choose>" +
            "WHERE EnCode = #{videoEnCode} AND VideoStatus = 0" +
            "</script>")
    void updateVideoStatNum(
            @Param("videoEnCode") String videoEnCode,
            @Param("type") String type,
            @Param("num") int num
    );
    @Select("""
        SELECT 
            v.ViewCount,
            v.LikeCount,
            v.CommentCount,
            v.ShareCount,
            v.CollectCount,
            uv.isLike,
            uv.IsCollect,
            uv.isShare,
            uv.lastPosition,
            uv.isComment
        FROM video v
        LEFT JOIN uservideointeract uv ON  v.VideoId=uv.VideoId
        WHERE v.IsDelete=0 
          AND v.VideoStatus=0 
          AND v.EnCode=#{enCode}
        """)
    List<VideoDateDTO> selectVideoOpDataByEnCode(@Param("enCode") String enCode);


    @Insert({
            "INSERT INTO userviewhistory (",
            "    id,",
            "    UserId,",
            "    VideoId,",
            "    watchTime,",
            "    duration",
            ") VALUES (",
            "    #{historyId},",
            "    IFNULL((SELECT UserId FROM `user` WHERE EnCode = #{userEncode}), 0),",
            "    IFNULL((SELECT VideoId FROM video WHERE EnCode = #{videoEnCode}), 0),",
            "    #{watchTime},",  // 保留你传入的点击时间
            "    #{watchLongTime}",
            ")"
    })
    int insertUserVideohietory(
            @Param("historyId")      String historyId,
            @Param("userEncode")     String userEncode,
            @Param("videoEnCode")    String videoEnCode,
            @Param("watchTime")      String watchTime,
            @Param("watchLongTime")  Integer watchLongTime
    );


    @Select({
            "<script>",
            "SELECT ",
            "v.VideoId             AS videoId,",
            "v.EnCode              AS enCode,",
            "v.VideoName           AS videoName,",
            "v.VideoImageUrl       AS videoImageUrl,",
            "v.VideoUrl            AS videoUrl,",
            "v.VideoTime           AS videoTime,",
            "v.ViewCount           AS viewCount,",
            "v.LikeCount           AS likeCount,",
            "v.CommentCount        AS commentCount,",
            "v.ShareCount          AS shareCount,",
            "v.CollectCount        AS collectCount,",
            "v.IsFeature           AS isFeature,",
            "v.VideoResolution     AS videoResolution,",
            "v.IsCollection        AS isCollection,",
            "v.VideoSort           AS videoSort,",
            "v.IsDelete            AS isDelete,",
            "v.VideoStatus         AS videoStatus,",
            "v.CreateTime          AS createTime,",
            "v.Introduction        AS introduction,",
            "u.RealName            AS publisherName,",
            "vh.watchTime          AS watchTime,",
            "vh.duration           AS duration ",
            "FROM userviewhistory vh ",
            "LEFT JOIN video v ON vh.VideoId = v.VideoId ",
            "LEFT JOIN user u ON v.PublisherId = u.UserId ",
            "WHERE v.IsDelete = 0 ",
            "AND v.VideoStatus = 0 ",
            "AND vh.UserId = (SELECT UserId FROM user WHERE EnCode = #{enCode}) ",
            "</script>"
    })
    @Results({
            @Result(column = "videoId",
                    property = "typeNames",
                    many = @Many(select = "getVideoTypeList"))
    })
    List<UserVideoHistoryDTO> getuserViewHistory(String enCode);

    @Select("SELECT vt.TypeName " +
            "FROM videoandtype vat " +
            "JOIN videotype vt ON vat.TypeId = vt.VideoTypeId " +
            "WHERE vat.VideoId = #{videoId}")
    List<String> getVideoTypeList(String videoId);

    @Select({
            "<script>",
            "SELECT ",
            // 你需要的视频字段 ↓↓↓
            "v.enCode,",
            "v.videoName,",
            "v.videoImageUrl,",
            "v.videoUrl,",
            "v.videoTime,",
            "v.viewCount,",
            "v.likeCount,",
            "v.commentCount,",
            "v.shareCount,",
            "v.collectCount,",
            "v.isFeature,",
            "v.videoResolution,",
            "v.isCollection,",
            "v.videoSort,",
            "v.isDelete,",
            "v.videoStatus,",
            "v.createTime,",
            "v.introduction,",

            "u.NickName AS publisherName ",

            "FROM video v ",
            "LEFT JOIN `user` u ON v.publisherId = u.userId ",

            "WHERE 1=1 ",
            "<if test='keyword != null and keyword != \"\"'>",
            "AND (v.videoName LIKE CONCAT('%', #{keyword}, '%') ",
            "OR v.introduction LIKE CONCAT('%', #{keyword}, '%')) ",
            "</if>",

            "AND v.isDelete = 0 ",
            "AND v.videoStatus = 0 ",

            "ORDER BY v.viewCount DESC, v.createTime DESC ",
            "</script>"
    })
    List<VideoTagDTO> selectVideoTagByCode(String keyword);

    @Select("""
        SELECT
            vc.VideoCommentId AS videoCommentId,
            vc.VideoId AS videoId,
            vc.UserId AS userId,
            vc.ParentId AS parentId,
            vc.Content AS content,
            vc.LikeCount AS likeCount,
            vc.Status AS status,
            vc.CreateTime AS createTime,
            vc.IsRead AS isRead,
            
            u.NickName AS replyNickName,
            u.Avatar AS replyAvatar,
            u.EnCode AS replyEncode,
            
            pvc.Content AS originalContent,
            
            v.VideoName AS videoName,
            v.VideoImageUrl AS videoImageUrl,
            v.Encode AS videoEncode
        
        FROM videocomment vc
        
        JOIN video v 
            ON vc.VideoId = v.VideoId
            AND v.VideoStatus = 0
            AND v.IsDelete = 0
        
        LEFT JOIN videocomment pvc 
            ON vc.ParentId = pvc.VideoCommentId
            AND pvc.Status = 1
        
        LEFT JOIN user u 
            ON vc.UserId = u.UserId
        
        WHERE
     
            v.PublisherId = (SELECT UserId FROM user WHERE Encode = #{userEncode})

            OR (
                pvc.UserId = (SELECT UserId FROM user WHERE Encode = #{userEncode})
                AND vc.ParentId IS NOT NULL
                AND vc.ParentId != ''
                AND vc.ParentId != '0'
            )
            
            AND vc.Status = 1
            AND vc.UserId != (SELECT UserId FROM user WHERE Encode = #{userEncode})
        
        ORDER BY vc.CreateTime DESC
        """)
    @Results({
            @Result(column = "videoCommentId", property = "videoCommentId"),
            @Result(column = "videoId", property = "videoId"),
            @Result(column = "userId", property = "userId"),
            @Result(column = "parentId", property = "parentId"),
            @Result(column = "content", property = "content"),
            @Result(column = "likeCount", property = "likeCount"),
            @Result(column = "status", property = "status"),
            @Result(column = "createTime", property = "createTime"),
            @Result(column = "isRead", property = "isRead"),

            @Result(column = "replyNickName", property = "replyNickName"),
            @Result(column = "replyAvatar", property = "replyAvatar"),
            @Result(column = "replyEncode", property = "replyEncode"),

            @Result(column = "originalContent", property = "originalContent"),

            @Result(column = "videoName", property = "videoName"),
            @Result(column = "videoImageUrl", property = "videoImageUrl"),
            @Result(column = "videoEncode", property = "videoEncode")
    })
    List<VideoCommentVO> GetOtherMessageDate(@Param("userEncode") String userEncode);

    @Update({
            "<script>",
            "UPDATE videocomment ",
            "SET IsRead = 1 ",
            "WHERE VideoCommentId IN ",
            "<foreach collection='commentId' item='id' open='(' separator=',' close=')'>",
            "    #{id}",
            "</foreach>",
            "</script>"
    })
    void UpdateUserCommentMessage(
            @Param("commentId") List<String> commentId
    );

    @Select("""
        SELECT
            vc.VideoCommentId AS videoCommentId,
            vc.VideoId AS videoId,
            vc.UserId AS userId,
            vc.ParentId AS parentId,
            vc.Content AS content,
            vc.LikeCount AS likeCount,
            vc.Status AS status,
            vc.CreateTime AS createTime,
            vc.IsRead AS isRead,

            u.NickName AS replyNickName,
            u.Avatar AS replyAvatar,
            u.EnCode AS replyEncode,

            pvc.Content AS originalContent,

            v.VideoName AS videoName,
            v.VideoImageUrl AS videoImageUrl,
            v.Encode AS videoEncode

        FROM videocomment vc

        JOIN video v
            ON vc.VideoId = v.VideoId
            AND v.VideoStatus = 0
            AND v.IsDelete = 0

        LEFT JOIN videocomment pvc
            ON vc.ParentId = pvc.VideoCommentId
            AND pvc.Status = 1

        LEFT JOIN user u
            ON vc.UserId = u.UserId

        LEFT JOIN user me
            ON vc.UserId = me.UserId

        WHERE
            me.Encode = #{userEncode}
            AND vc.Status = 1

        ORDER BY vc.CreateTime DESC
        """)
    @Results({
            @Result(column = "videoCommentId", property = "videoCommentId"),
            @Result(column = "videoId", property = "videoId"),
            @Result(column = "userId", property = "userId"),
            @Result(column = "parentId", property = "parentId"),
            @Result(column = "content", property = "content"),
            @Result(column = "likeCount", property = "likeCount"),
            @Result(column = "status", property = "status"),
            @Result(column = "createTime", property = "createTime"),
            @Result(column = "isRead", property = "isRead"),
            @Result(column = "replyNickName", property = "replyNickName"),
            @Result(column = "replyAvatar", property = "replyAvatar"),
            @Result(column = "replyEncode", property = "replyEncode"),

            @Result(column = "originalContent", property = "originalContent"),

            @Result(column = "videoName", property = "videoName"),
            @Result(column = "videoImageUrl", property = "videoImageUrl"),
            @Result(column = "videoEncode", property = "videoEncode")
    })
    List<VideoCommentVO> getmysent(@Param("userEncode") String userEncode);

    @Update({
            "<script>",
            "UPDATE uservideointeract SET isRead = 1 WHERE id IN ",
            "<foreach collection='interactId' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    void UpdateReadUserCommentMessage(@Param("interactId") List<String> interactId);


    /**
     * 获取用户账户状态/违规记录
     */
// ====================== 账号状态 ======================
    @Select("""
        SELECT 
            a.AccountStatuId AS accountStatusId,
            a.UserId AS userId,
            a.AccountStatusType AS accountStatusType,
            a.StartTime AS startTime,
            a.EndTime AS endTime,
            a.Reason AS reason,
            a.CreateTime AS createTime,
            a.IsRead AS isRead
        FROM accountstatus a
        LEFT JOIN `user` u ON u.UserId = a.UserId
        WHERE u.EnCode = #{userEncode}
        """)
    List<AccountStatus> getUserAuditInfo(@Param("userEncode") String userEncode);

    // ====================== 审核记录 ======================
    @Select("""
        SELECT 
            uv.Id AS id,
            uv.UserId AS userId,
            uv.TargetType AS targetType,
            uv.Status AS status,
            uv.Content AS content,
            uv.IsRead AS isRead,
            uv.CreateTime AS createTime,
            uv.UpdateTime AS updateTime
        FROM userreview uv
        LEFT JOIN `user` u ON uv.UserId = u.UserId
        WHERE u.EnCode = #{userEncode}
        """)
    List<UserReview> getUserReview(@Param("userEncode") String userEncode);

    // 批量更新 账户状态消息 为已读
    @Update("<script>"
            + "UPDATE accountstatus SET isRead = 1 "
            + "WHERE AccountStatuId IN "
            + "<foreach collection='ids' open='(' separator=',' close=')' item='id'>"
            + "#{id}"
            + "</foreach>"
            + "</script>")
    void batchReadAccountStatus(@Param("ids") List<String> ids);

    // 批量更新 审核记录消息 为已读
    @Update("<script>"
            + "UPDATE userreview SET isRead = 1 "
            + "WHERE Id IN "
            + "<foreach collection='ids' open='(' separator=',' close=')' item='id'>"
            + "#{id}"
            + "</foreach>"
            + "</script>")
    void batchReadUserReview(@Param("ids") List<String> ids);

    @Insert("INSERT INTO VideoPending(" +
            "VideoPendingId, EnCode, VideoName, VideoDesc, VideoImageUrl, VideoUrl, " +
            "videoTypeId, " +  //
            "PublisherId, PublishCode, ReviewStatus, PublishTime, VideoTime, VideoSize, VideoFormat, " +
            "IsCollection, CollectionId, VideoStatus, AuditTime, EndAuditTime, IsDelete, " +
            "ReviewerCode, ReviewerId, CreateTime, UpdateTime) " +
            "VALUES(" +
            "#{videoPendingId}, #{enCode}, #{videoName}, #{videoDesc}, #{videoImageUrl}, #{videoUrl}, " +
            "#{videoTypeId}, " +  //
            "#{publisherId}, #{publishCode}, #{reviewStatus}, #{publishTime}, #{videoTime}, #{videoSize}, #{videoFormat}, " +
            "#{isCollection}, #{collectionId}, #{videoStatus}, #{auditTime}, #{endAuditTime}, #{isDelete}, " +
            "#{reviewerCode}, #{reviewerId}, #{createTime}, #{updateTime})")
    void insert(VideoPending pending);

    @Select("SELECT UserId FROM user WHERE EnCode = #{userEncode}")
    String getuseridbyencode(String userEncode);



    @Select("SELECT " +
            "v.EnCode, " +
            "vs.Resolution, " +
            "vs.VideoUrl, " +
            "vs.Width, " +
            "vs.Height, " +
            "vs.Bitrate, " +
            "vs.Size, " +
            "vs.Format, " +
            "vs.CreateTime " +
            "FROM videoresolution vs " +
            "LEFT JOIN video v ON v.VideoId = vs.VideoId " +
            "WHERE v.EnCode = #{enCode}")
    List<VideoResolutionDTO> getvideoresolution(@Param("enCode") String enCode);


    @Select("SELECT " +
            "vt.VideoTypeId, " +
            "vt.TypeCode, " +
            "vt.TypeName, " +
            "vt.CreateTime, " +
            "vt.UpdateTime " +
            "FROM videotype vt " +
            "LEFT JOIN videoandtype vat ON vat.TypeId = vt.VideoTypeId " +
            "LEFT JOIN video v ON v.VideoId = vat.VideoId " +
            "WHERE v.EnCode = #{encode}")
    List<VideoType> findVideoTypeByCode(@Param("encode") String encode);


    @Select("SELECT " +
            "realName, " +
            "nickName, " +
            "phone, " +
            "enCode, " +
            "email, " +
            "avatar, " +
            "gender, " +
            "birthDay, " +
            "roleBase, " +
            "uiMember, " +
            "leavel, " +
            "address, " +
            "country, " +
            "school, " +
            "language, " +
            "age " +
            "FROM user " +
            "WHERE UserId = #{publicshId}")
    UserMessageDTO findPublisherNameByCode(@Param("publicshId") String publicshId);

    @Select("SELECT " +
            "vat.AuditTagId AS auditTagId, " +
            "vat.TagName AS tagName, " +
            "vat.TagCode AS tagCode, " +
            "vat.TagType AS tagType, " +
            "vat.IsDelete AS isDelete, " +
            "vat.CreateTime AS createTime, " +
            "vat.UpdateTime AS updateTime " +
            "FROM video v " +
            "JOIN videopendingandaudittag vpa ON v.VideoId = vpa.VideoPendingId " +
            "JOIN videoaudittag vat ON vpa.AuditTagId = vat.AuditTagId " +
            "WHERE v.EnCode = #{encode}")
    List<VideoAuditTag> findVideoAuditTagByCode(@Param("encode") String encode);


    @Select("SELECT " +
            "u.realName, " +
            "u.nickName, " +
            "u.enCode, " +
            "u.email, " +
            "u.avatar, " +
            "u.gender, " +
            "uvi.isLike, " +     // 原样返回：0或1
            "uvi.isCollect, " +  // 原样返回：0或1
            "uvi.isComment, " +  // 原样返回：0或1
            "uvi.isShare, " +    // 原样返回：0或1
            "uvi.playPercent " + // 播放进度
            "FROM uservideointeract uvi " +
            "INNER JOIN video v ON v.VideoId = uvi.VideoId " +
            "INNER JOIN user u ON u.UserId = uvi.userId " +
            "WHERE v.EnCode = #{encode}")
    List<Userandvideinterseat> findUserMessageDTOByCode(@Param("encode") String encode);


    @Select("SELECT " +
            "vat.AuditTagId AS auditTagId, " +
            "vat.TagName AS tagName, " +
            "vat.TagCode AS tagCode, " +
            "vat.TagType AS tagType, " +
            "vat.IsDelete AS isDelete, " +
            "vat.CreateTime AS createTime, " +
            "vat.UpdateTime AS updateTime " +
            "FROM videopending vp " +
            "JOIN videopendingandaudittag vpa ON vp.VideoPendingId = vpa.VideoPendingId " +
            "JOIN videoaudittag vat ON vpa.AuditTagId = vat.AuditTagId " +
            "WHERE vp.EnCode = #{encode}")
    List<VideoAuditTag> findVideopendingAuditTagByCode(@Param("encode") String encode);

    @Select("SELECT * FROM video WHERE EnCode = #{encode}")
    Video findVideoByCode(String encode);

    @Update("UPDATE video SET VideoStatus = #{status} WHERE EnCode = #{encode}")
    void updateVideoStatus(String encode, int status);



    @Select("SELECT VideoId FROM video WHERE EnCode = #{videoEncode}")
    String getvideoidbyencode(String videoEncode);


    @Insert("INSERT INTO VideoResources ("
            + "VideoResourcesId, "
            + "VideoId, ResourceName, ResourceType, ResourceFormat, ResourceUrl, ResourceSize, "
            + "DownloadCount, UploadTime, UserId, IsDelete, ResourceDesc"
            + ") VALUES ("
            + "#{resourseid}, "
            + "#{videoid}, #{name}, #{type}, #{format}, #{fileurl}, #{size}, "
            + "0, NOW(), #{userid}, 0, #{recoursedese}"
            + ")")
    void insertUserResources(String resourseid, String fileurl, String name, String type, String format, int size, String recoursedese, String videoid, String userid);

    @Update("update video set IsDelete=1 where EnCode=#{enCode}")
    void deleteVideo(String enCode);

    @Update("update videoresources set IsDelete=1 where videoresourcesid= #{id}")
    void deleteVideoResource(String id);

    @Update("update videopending set IsDelete=1 where EnCode= #{enCode}")
    void deletepending(String enCode);

    @Select("SELECT " +
            "    (SELECT COUNT(*) FROM video WHERE IsDelete = 0) AS totalVideos, " +
            "    SUM(CASE WHEN vp.VideoStatus = 1 THEN 1 ELSE 0 END) AS passedVideos, " +
            "    SUM(CASE WHEN vp.VideoStatus = 2 THEN 1 ELSE 0 END) AS pendingVideos, " +
            "    (SELECT IFNULL(SUM(ViewCount), 0) FROM video WHERE IsDelete = 0) AS totalViews, " +
            "    ROUND(SUM(CASE WHEN vp.VideoStatus = 1 THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(vp.VideoPendingId), 0), 2) AS passRate, " +
            "    ROUND(SUM(CASE WHEN vp.VideoStatus = 2 THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(vp.VideoPendingId), 0), 2) AS pendingRate " +
            "FROM videopending vp " +
            "WHERE vp.IsDelete = 0")
    Map<String, Object> getVideoStats();


    @Select("SELECT " +
            "    vt.TypeName AS name, " +
            "    COUNT(DISTINCT v.VideoId) AS count, " +
            "    ROUND(COUNT(DISTINCT v.VideoId) * 100.0 / total.total, 0) AS percent " +
            "FROM videotype vt " +
            "LEFT JOIN videoandtype vat ON vt.VideoTypeId = vat.TypeId " +
            "LEFT JOIN video v ON vat.VideoId = v.VideoId AND v.IsDelete = 0 " +
            "LEFT JOIN (SELECT COUNT(DISTINCT VideoId) AS total FROM video WHERE IsDelete = 0) AS total ON 1=1 " +
            "GROUP BY vt.TypeName, total.total " +
            "ORDER BY count DESC")
    List<Map<String, Object>> getVideoTypeDistribution();

    @Select("SELECT COUNT(DISTINCT VideoCommentId) AS commentCount FROM videocomment WHERE Status = 1")
    Integer getTotalCommentCount();

    @Select("SELECT COUNT(*) AS modelCount FROM aiconfig")
    Integer getAiModelCount();

    @Select("SELECT COUNT(*) FROM videoresolution")
    Integer getTotalFileCount();

    @Update("UPDATE videopending SET VideoImageUrl = #{newCoverUrl} WHERE VideoPendingId = #{videoPendingId}")
    void updateVideoImageUrl(String videoPendingId, String newCoverUrl);
}


