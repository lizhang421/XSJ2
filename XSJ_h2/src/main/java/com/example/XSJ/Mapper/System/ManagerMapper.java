package com.example.XSJ.Mapper.System;

import com.example.XSJ.Pojo.PoolExtractRule;
import com.example.XSJ.Pojo.System.HomeResources;
import com.example.XSJ.Pojo.System.SysScoewConfig;
import com.example.XSJ.Pojo.System.SysScoreConfig;
import com.example.XSJ.Pojo.System.UserReview;
import com.example.XSJ.Pojo.User.Report;
import com.example.XSJ.Pojo.User.ReportVO;
import com.example.XSJ.Pojo.User.User;
import com.example.XSJ.Pojo.User.UserUpdateDTO;
import com.example.XSJ.Pojo.UserDTO.UserMessageDTO;
import com.example.XSJ.Pojo.Video.Video;
import com.example.XSJ.Pojo.Video.VideoAuditTag;
import com.example.XSJ.Pojo.Video.VideoPoolConfig;
import com.example.XSJ.Pojo.VideoDTo.VideoPendingDTO;
import com.example.XSJ.Pojo.VideoDTo.VideoResolutionDTO;
import jakarta.validation.constraints.Email;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ManagerMapper {


    @Select("select EnCode,PassWord,RoleBase from user where Phone =#{phone}")
    User getUserMessageByPhone(String phone);

    @Select("SELECT " +
            "vp.VideoPendingId AS videoPendingId, " +
            "vp.EnCode AS enCode, " +
            "vp.VideoName AS videoName, " +
            "vp.VideoDesc AS videoDesc, " +
            "vp.VideoImageUrl AS videoImageUrl, " +
            "vp.VideoUrl AS videoUrl, " +
            "vp.PublisherId AS publisherId, " +
            "vp.PublishCode AS publishCode, " +
            "vp.ReviewStatus AS reviewStatus, " +
            "vp.PublishTime AS publishTime, " +
            "vp.VideoTime AS videoTime, " +
            "vp.VideoSize AS videoSize, " +
            "vp.VideoFormat AS videoFormat, " +
            "vp.IsCollection AS isCollection, " +
            "vp.CollectionId AS collectionId, " +
            "vp.VideoStatus AS videoStatus, " +
            "vp.AuditTime AS auditTime, " +
            "vp.EndAuditTime AS endAuditTime, " +
            "vp.IsDelete AS isDelete, " +
            "vp.ReviewerCode AS reviewerCode, " +
            "vp.ReviewerId AS reviewerId, " +
            "vp.CreateTime AS createTime, " +
            "vp.UpdateTime AS updateTime, " +
            "vp.VideoTypeId AS videoTypeId, " +
            "vt.TypeCode AS typeCode, " +
            "vt.TypeName AS typeName " +
            "FROM videopending vp " +
            "LEFT JOIN videotype vt ON vp.VideoTypeId = vt.VideoTypeId")
    List<VideoPendingDTO> getauditvideo();


    @Update("UPDATE videopending SET VideoStatus = #{reviewStatus},ReviewStatus=2,ReviewerCode=#{userEncode},ReviewerId=#{managerid}   WHERE VideoPendingId = #{videoPendingId}")
    void auditVideo(String videoPendingId, int reviewStatus, String managerid, String userEncode);

    @Insert("INSERT INTO video ( " +
            "    VideoId, " +
            "    EnCode, " +
            "    VideoName, " +
            "    Introduction, " +
            "    VideoImageUrl, " +
            "    VideoUrl, " +
            "    PublisherId, " +
            "    CreateTime, " +
            "    VideoTime, " +
            "    VideoSize, " +
            "    VideoFormat, " +
            "    IsCollection, " +
            "    CollectionId, " +
            "    ReviewerId " +
            ") " +
            "SELECT " +
            "    #{videoId}, " +
            "    p.EnCode, " +
            "    p.VideoName, " +
            "    p.VideoDesc, " +
            "    p.VideoImageUrl, " +
            "    p.VideoUrl, " +
            "    p.PublisherId, " +
            "    p.PublishTime, " +
            "    p.VideoTime, " +
            "    p.VideoSize, " +
            "    p.VideoFormat, " +
            "    p.IsCollection, " +
            "    p.CollectionId, " +
            "    (SELECT UserId FROM user WHERE EnCode = #{userEncode}) " +
            "FROM videopending p " +
            "WHERE p.VideoPendingId = #{videoPendingId}")
    int insertVideoFromPending(
            @Param("videoPendingId") String videoPendingId,
            @Param("videoId") String videoId,
            @Param("userEncode") String userEncode
    );


    @Insert("INSERT INTO videoandtype (" +
            "VideoAndTypeId, " +
            "VideoId, " +       // 这里是正式视频ID
            "TypeId, " +        // 视频类型ID
            "CreateTime, " +
            "UpdateTime " +
            ") " +
            "SELECT " +
            "    CONCAT('VA_', REPLACE(UUID(), '-', '')), " +
            "    #{videoId}, " +             // 正式视频ID ✔
            "    vp.VideoTypeId, " +
            "    NOW(), " +
            "    NOW() " +
            "FROM videopending vp " +
            "WHERE vp.VideoPendingId = #{videoPendingId}")
        //
    int auditVideoandType(
            @Param("videoPendingId") String videoPendingId,  // 待审核
            @Param("videoId") String videoId                 // 正式视频ID
    );


    @Select("SELECT * FROM video WHERE VideoId = #{videoId}")
    Video getVideoById(String videoId);


    @Insert("INSERT INTO videoresolution (" +
            "Id, VideoId, Resolution, VideoUrl, Width, Height, Bitrate, Size, Format, Duration, CreateTime) " +
            "VALUES (" +
            "#{id}, #{videoId}, #{resolution}, #{videoUrl}, #{width}, #{height}, #{bitrate}, #{size}, #{format}, #{duration}, NOW())")
    int insertVideoResolution(VideoResolutionDTO dto);

    @Update("UPDATE videopending SET " +
            "    VideoStatus = #{reviewStatus}, " +
            "    ReviewStatus = 2, " +
            "    AuditTime = NOW(), " +
            "    EndAuditTime = NOW() " +
            "WHERE VideoPendingId = #{videoPendingId}")
    void updateAuditStatus(
            @Param("videoPendingId") String videoPendingId,
            @Param("reviewStatus") int reviewStatus
    );

    @Select("SELECT vt.PoolCaetGory " +
            "FROM videotype vt " +
            "WHERE vt.VideoTypeId = (" +
            "    SELECT vp.VideoTypeId " +
            "    FROM videopending vp " +
            "    WHERE vp.VideoPendingId = #{videoPendingId}" +
            ")")
    String getpendingpool(@Param("videoPendingId") String videoPendingId);

    @Select("SELECT VideoPoolConfigId FROM videopoolconfig " +
            "WHERE PoolCaetGory = #{poolCaetGory} AND PoolType = '冷启动池'")
    String getpoolid(@Param("poolCaetGory") String poolCaetGory);

    @Insert("INSERT INTO videoandpoolcofig(VideoAndPoolId, VideoId, PoolId) " +
            "VALUES(#{vapid}, #{videoid}, #{poolid})")
    void insertPool(
            @Param("vapid") String vapid,
            @Param("poolid") String poolid,
            @Param("videoid") String videoid
    );


    @Select("<script>"
            + "SELECT id, UserId, TargetType, Status, Content, IsRead, CreateTime, UpdateTime, RecourseUrl "
            + "FROM userreview "
            + "WHERE Status IN "
            + "<foreach collection='status' item='item' open='(' separator=',' close=')'>"
            + "#{item}"
            + "</foreach>"
            + " ORDER BY CreateTime DESC"
            + "</script>")
    List<UserReview> getShenHeImage(@Param("status") List<Integer> status);


    @Select("SELECT * FROM videoaudittag")
    List<VideoAuditTag> getauditVideoResult();


    @Insert({
            "<script>",
            "INSERT INTO videopendingandaudittag (",
            "    VideoPendingAndAuditTagId,",  // 主键
            "    VideoPendingId,",             // 视频ID
            "    AuditTagId",                  // 审核标签ID
            ")",
            "VALUES",
            "<foreach collection='videoAuditTagId' item='tagId' separator=','>",
            "    (",
            "        REPLACE(UUID(), '-', ''),",  // 生成无横杠UUID主键
            "        #{videoPendingId},",
            "        #{tagId}",
            "    )",
            "</foreach>",
            "</script>"
    })
    void insertAuditStatus(
            @Param("videoPendingId") String videoPendingId,
            @Param("videoAuditTagId") List<String> videoAuditTagId
    );


    @Update("<script>"
            + "UPDATE `user` u "
            + "INNER JOIN userreview ur ON u.UserId = ur.UserId "
            + "SET "
            + "   u.Avatar = CASE WHEN ur.TargetType = 'AVATAR' THEN ur.RecourseUrl ELSE u.Avatar END, "
            + "   u.BackImage = CASE WHEN ur.TargetType = 'BACKGROUND' THEN ur.RecourseUrl ELSE u.BackImage END "
            + "WHERE ur.id = #{userviewid}"
            + "</script>")
    void uploadShenHeImage(@Param("userviewid") String userviewid);


    @Update("UPDATE userreview " +
            "SET Status = #{status}, " +
            "Content = #{content}, " +
            "IsRead = 0, " +
            "UpdateTime = NOW() " +
            "WHERE id = #{userviewid}")
    void updateReviewStatus(
            @Param("userviewid") String userviewid,
            @Param("status") Integer status,
            @Param("content") String content
    );

    //查找全局唯一配置
    @Select("SELECT Id, Category, FinishRateWeight, LikeRateWeight, CommentRateWeight, " +
            "CollectRateWeight, ShareRateWeight, TimeScoreWeight, ScoreWindowHours, " +
            "TotalWeightCheck, Enable, Version, Remark, CreateBy, CreateTime, UpdateBy, UpdateTime " +
            "FROM SysScoreConfig WHERE Category = '*' AND Enable = 1")
    SysScoreConfig getSysScoreConfig();


    @Select("SELECT * FROM Video " +
            "WHERE IsDelete = 0 AND ReviewStatus = 1 " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<Video> getVideoList(
            @Param("pageSize") int pageSize,
            @Param("offset") int offset
    );


    @Update("UPDATE Video SET SortWeight = #{sortWeight} WHERE VideoId = #{videoId}")
    int updateVideoSortWeight(Video video);


    @Select("SELECT vc.* FROM videopoolconfig vc " +
            "LEFT JOIN videoandpoolcofig vap ON vap.PoolId = vc.VideoPoolConfigId " +
            "LEFT JOIN video v ON v.VideoId = vap.VideoId " +
            "WHERE v.VideoId = #{videoId} " +
            "AND v.IsDelete = 0 " +
            "AND v.VideoStatus = 0 " +
            "AND vap.Status = 1 " +
            "LIMIT 1")
    VideoPoolConfig getVideoPoolConfig(@Param("videoId") String videoId);


    @Select("SELECT VideoPoolConfigId FROM VideoPoolConfig " +
            "WHERE PoolCaetGory = #{useCategory} " +
            "AND PoolType = #{targetPoolType}")
    String getRealPoolIdByCategoryAndType(
            @Param("useCategory") String useCategory,
            @Param("targetPoolType") String targetPoolType
    );

    @Update("UPDATE VideoAndPoolCofig " +
            "SET Status = 0, " +
            "LeavePoolTime = NOW(), " +
            "LeaveReason = #{leaveReason} " +
            "WHERE VideoId = #{videoId} " +
            "AND PoolId = #{poolId} " +
            "AND Status = 1")
    int leavePool(
            @Param("videoId") String videoId,
            @Param("poolId") String poolId,
            @Param("leaveReason") String leaveReason
    );

    @Select("SELECT COUNT(*) FROM VideoAndPoolCofig " +
            "WHERE VideoId = #{videoId} " +
            "AND PoolId = #{poolId} " +
            "AND Status = 1")
    int isInPool(@Param("videoId") String videoId,
                 @Param("poolId") String poolId);

    @Insert("INSERT INTO VideoAndPoolCofig " +
            "(VideoAndPoolId, VideoId, PoolId, JoinPoolTime, Status, JoinReason) " +
            "VALUES (REPLACE(UUID(),'-',''), #{videoId}, #{poolId}, NOW(), 1, #{reason})")
    void joinPool(
            @Param("videoId") String videoId,
            @Param("poolId") String poolId,
            @Param("reason") String reason
    );

    @Update("UPDATE Video " +
            "SET CurrentPoolType = #{currentPoolType} " +
            "WHERE VideoId = #{videoId}")
    void updateVideoCurrentPool(Video video);

    @Select("SELECT PoolId FROM VideoAndPoolCofig " +
            "WHERE VideoId = #{videoId} AND Status = 1 " +
            "LIMIT 1")
    String getCurrentVideoPoolId(@Param("videoId") String videoId);

    @Update("UPDATE VideoAndPoolCofig " +
            "SET Status = 0, " +
            "LeavePoolTime = NOW(), " +
            "LeaveReason = #{reason} " +
            "WHERE VideoId = #{videoId} AND Status = 1")
    void leaveAllPools(
            @Param("videoId") String videoId,
            @Param("reason") String reason
    );


    @Select("SELECT * FROM User " +
            "WHERE EnCode = #{userEncode}")
    User getuserdatebyencode(String userEncode);

    @Select("<script>"
            + "SELECT "
            + "realName, "
            + "nickName, "
            + "phone, "
            + "enCode, "
            + "email, "
            + "avatar, "
            + "gender, "
            + "birthDay, "
            + "roleBase, "
            + "uiMember, "
            + "creatTime, "
            + "leavel, "
            + "address, "
            + "country, "
            + "school, "
            + "status, "
            + "language, "
            + "age "
            + "FROM User "
            + "WHERE 1=1 "

            + "<if test='status != null'>"
            + "   AND Status = #{status} "
            + "</if>"

            + "<if test='roleBase != null'>"
            + "   AND roleBase = #{roleBase} "
            + "</if>"

            + "<if test='key != null and key != \"\"'>"
            + "   AND ("
            + "       nickName LIKE CONCAT('%', #{key}, '%') "
            + "       OR realName LIKE CONCAT('%', #{key}, '%') "
            + "       OR email LIKE CONCAT('%', #{key}, '%') "
            + "       OR phone LIKE CONCAT('%', #{key}, '%') "
            + "       OR enCode LIKE CONCAT('%', #{key}, '%') "
            + "       OR country LIKE CONCAT('%', #{key}, '%') "
            + "       OR language LIKE CONCAT('%', #{key}, '%') "
            + "       OR address LIKE CONCAT('%', #{key}, '%') "
            + "   )"
            + "</if>"

            + "ORDER BY creatTime DESC "
            + "LIMIT #{offset}, #{pageSize}"
            + "</script>")
    List<UserMessageDTO> getUserList(
            @Param("offset") int offset,
            @Param("pageSize") int pageSize,
            @Param("status") Integer status,    // 重点：Integer 允许不传
            @Param("roleBase") Integer roleBase,// 重点：Integer 允许不传
            @Param("key") String key
    );

    @Select("<script>"
            + "SELECT COUNT(*) FROM User "
            + "WHERE 1=1 "

            + "<if test='status != null'>"
            + "   AND Status = #{status} "
            + "</if>"

            + "<if test='roleBase != null'>"
            + "   AND roleBase = #{roleBase} "
            + "</if>"

            + "<if test='key != null and key != \"\"'>"
            + "   AND ("
            + "       nickName LIKE CONCAT('%', #{key}, '%') "
            + "       OR realName LIKE CONCAT('%', #{key}, '%') "
            + "       OR email LIKE CONCAT('%', #{key}, '%') "
            + "       OR phone LIKE CONCAT('%', #{key}, '%') "
            + "       OR enCode LIKE CONCAT('%', #{key}, '%') "
            + "       OR country LIKE CONCAT('%', #{key}, '%') "
            + "       OR language LIKE CONCAT('%', #{key}, '%') "
            + "       OR address LIKE CONCAT('%', #{key}, '%') "
            + "   )"
            + "</if>"
            + "</script>")
    int getUserCount(
            @Param("status") Integer status,     // 重点
            @Param("roleBase") Integer roleBase, // 重点
            @Param("key") String key
    );


    @Update("UPDATE Video " +
            "SET VideoStatus = 0 " +
            "WHERE EnCode = #{encode}")
    void updateReplyVideo(String encode);

    @Update("UPDATE User " +
            "SET Status = #{status} " +
            "WHERE EnCode = #{encode}")
    void updateUserStatus(String encode, Integer status);


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
            "creatTime, " +
            "leavel, " +
            "address, " +
            "country, " +
            "school, " +
            "language, " +
            "age, " +
            "status " +
            "FROM User " +
            "WHERE enCode = #{userEncode}")
    UserMessageDTO getManagerMessage(@Param("userEncode") String userEncode);


    @Select("SELECT id, category, pool_type, " +
            "top_weight_layer_ratio, mid_weight_layer_ratio, bottom_weight_layer_ratio, " +
            "top_weight_extract_ratio, mid_weight_extract_ratio, bottom_weight_extract_ratio, " +
            "layer_ratio_check, extract_ratio_check, " +
            "top_weight_min_threshold, mid_weight_min_threshold, bottom_weight_min_threshold, " +
            "enable, version, remark, create_by, create_time, update_by, update_time " +
            "FROM poolextractrule " +
            "WHERE enable = 1")
    List<PoolExtractRule> getSysConfig();


    @Update("<script>"
            + "UPDATE sysscoreconfig "
            + "<set>"
            + "  <if test='likeRateWeight != null'>LikeRateWeight = #{likeRateWeight},</if>"
            + "  <if test='commentRateWeight != null'>CommentRateWeight = #{commentRateWeight},</if>"
            + "  <if test='collectRateWeight != null'>CollectRateWeight = #{collectRateWeight},</if>"
            + "  <if test='shareRateWeight != null'>ShareRateWeight = #{shareRateWeight},</if>"
            + "  <if test='finishRateWeight != null'>FinishRateWeight = #{finishRateWeight},</if>"
            + "  <if test='timeScoreWeight != null'>TimeScoreWeight = #{timeScoreWeight},</if>"
            + "  <if test='scoreWindowHours != null'>ScoreWindowHours = #{scoreWindowHours},</if>"
            + "  <if test='totalWeightCheck != null'>TotalWeightCheck = #{totalWeightCheck},</if>"
            + "  <if test='enable != null'>Enable = #{enable},</if>"
            + "  <if test='remark != null'>Remark = #{remark},</if>"
            + "  <if test='updateBy != null'>UpdateBy = #{updateBy}</if>"
            + "</set>"
            + " WHERE id = #{id}"
            + "</script>")
    int updateById(SysScoewConfig sysScoewConfig);

    @Update("<script>"
            + "UPDATE poolextractrule "
            + "<set>"
            + "  <if test='category != null'>category = #{category},</if>"
            + "  <if test='poolType != null'>pool_type = #{poolType},</if>"
            + "  <if test='topWeightLayerRatio != null'>top_weight_layer_ratio = #{topWeightLayerRatio},</if>"
            + "  <if test='midWeightLayerRatio != null'>mid_weight_layer_ratio = #{midWeightLayerRatio},</if>"
            + "  <if test='bottomWeightLayerRatio != null'>bottom_weight_layer_ratio = #{bottomWeightLayerRatio},</if>"
            + "  <if test='topWeightExtractRatio != null'>top_weight_extract_ratio = #{topWeightExtractRatio},</if>"
            + "  <if test='midWeightExtractRatio != null'>mid_weight_extract_ratio = #{midWeightExtractRatio},</if>"
            + "  <if test='bottomWeightExtractRatio != null'>bottom_weight_extract_ratio = #{bottomWeightExtractRatio},</if>"
            + "  <if test='topWeightMinThreshold != null'>top_weight_min_threshold = #{topWeightMinThreshold},</if>"
            + "  <if test='midWeightMinThreshold != null'>mid_weight_min_threshold = #{midWeightMinThreshold},</if>"
            + "  <if test='bottomWeightMinThreshold != null'>bottom_weight_min_threshold = #{bottomWeightMinThreshold},</if>"
            + "  <if test='enable != null'>enable = #{enable},</if>"
            + "  <if test='remark != null'>remark = #{remark},</if>"
            + "  <if test='updateBy != null'>update_by = #{updateBy},</if>"
            + "  <if test='updateTime != null'>update_time = #{updateTime}</if>"
            + "</set>"
            + " WHERE id = #{id}"
            + "</script>")
    int updatePoolById(PoolExtractRule poolExtractRule);

    @Insert("<script>"
            + "INSERT INTO poolextractrule ("
            + "  category,"
            + "  pool_type,"
            + "  top_weight_layer_ratio,"
            + "  mid_weight_layer_ratio,"
            + "  bottom_weight_layer_ratio,"
            + "  top_weight_extract_ratio,"
            + "  mid_weight_extract_ratio,"
            + "  bottom_weight_extract_ratio,"
            + "  top_weight_min_threshold,"
            + "  mid_weight_min_threshold,"
            + "  bottom_weight_min_threshold,"
            + "  enable,"
            + "  remark,"
            + "  create_by,"
            + "  create_time,"
            + "  update_by,"
            + "  update_time"
            + ") VALUES ("
            + "  #{category},"
            + "  #{poolType},"
            + "  #{topWeightLayerRatio},"
            + "  #{midWeightLayerRatio},"
            + "  #{bottomWeightLayerRatio},"
            + "  #{topWeightExtractRatio},"
            + "  #{midWeightExtractRatio},"
            + "  #{bottomWeightExtractRatio},"
            + "  #{topWeightMinThreshold},"
            + "  #{midWeightMinThreshold},"
            + "  #{bottomWeightMinThreshold},"
            + "  #{enable},"
            + "  #{remark},"
            + "  #{createBy},"
            + "  #{createTime},"
            + "  #{updateBy},"
            + "  #{updateTime}"
            + ")"
            + "</script>")
    int insert(PoolExtractRule poolExtractRule);

    @Delete("<script>"
            + "DELETE FROM poolextractrule WHERE id = #{poolId}"
            + "</script>")
    int deletePoolById(Long poolId);

    @Select("select * from poolextractrule where id=#{poolId}")
    PoolExtractRule selectById(Long poolId);

    @Select({
            "<script>",
            "SELECT r.*,",
            "v.EnCode,",
            "v.VideoName,",
            "v.VideoImageUrl,",
            "c.Content AS commentContent ",
            "FROM Report r ",
            "LEFT JOIN Video v ON r.ReportType = 1 AND r.TargetId = v.VideoId ",
            "LEFT JOIN VideoComment c ON r.ReportType = 2 AND r.TargetId = c.VideoCommentId ",
            "<where>",
            "   <if test=\"status != null\">AND r.Status = #{status}</if>",
            "   <if test=\"type != null\">AND r.ReportType = #{type}</if>",
            "   <if test=\"startTime != null\">AND r.CreateTime &gt;= #{startTime}</if>",
            "   <if test=\"endTime != null\">AND r.CreateTime &lt;= #{endTime}</if>",
            "</where>",
            "ORDER BY r.CreateTime DESC",
            "</script>"
    })
    List<ReportVO> getjubao(
            @Param("status") Integer status,
            @Param("type") Integer type,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );


    @Select("select * from report where Id=#{jubaoId}")
    Report selectreportbyid(String jubaoId);

    @Update("update report set Status=#{jieguo} ,UserIsRead=0,TargetUserIsRead=0  where Id=#{jubaoId}")
    int updateReportStatus(String jubaoId, int jieguo);


    @Update("update video set VideoStatus=2 where EnCode= #{targetId}")
    int updateVideoStatus(String targetId);


    @Delete("delete from VideoComment where VideoCommentId= #{targetId}")
    int deleteComment(String targetId);



    @Insert("insert into accountstatus(AccountStatuId,UserId,AccountStatusType,reason) values(#{accountId},#{userid},#{solvechufa},#{管理员审核处理})")
    int insertUserPunishment(String accountId, String userid, Integer solvechufa, String 管理员审核处理);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM user ",
            "WHERE 1=2 ",
            "<if test='phone != null and phone != \"\"'>",
            "   OR phone = #{phone} ",
            "</if>",
            "<if test='email != null and email != \"\"'>",
            "   OR email = #{email} ",
            "</if>",
            "</script>"
    })
    int checkPhoneOrEmailExist(
            @Param("phone") String phone,
            @Param("email") @Email(message = "邮箱格式不正确") String email
    );

    @Update({
            "<script>",
            "UPDATE user ",
            "<set>",
            "  <if test='dto.realName != null'>realname = #{dto.realName},</if>",
            "  <if test='dto.nickName != null'>nickname = #{dto.nickName},</if>",
            "  <if test='dto.phone != null'>phone = #{dto.phone},</if>",
            "  <if test='dto.email != null'>email = #{dto.email},</if>",
            "  <if test='dto.gender != null'>gender = #{dto.gender},</if>",
            "  <if test='dto.school != null'>school = #{dto.school},</if>",
            "  <if test='dto.leavel != null'>leavel = #{dto.leavel},</if>",
            "  <if test='dto.address != null'>address = #{dto.address},</if>",
            "  <if test='dto.avatar != null'>avatar = #{dto.avatar},</if>",
            "</set>",
            "WHERE EnCode = #{userEncode}",
            "</script>"
    })
    int updateUserMessage(
            @Param("userEncode") String userEncode,
            @Param("dto") UserUpdateDTO userUpdateDTO
    );


    @Select("select * from homeresources")
    List<HomeResources> getSysResourceConfig();

    @Update("update homeresources set Address=#{url} where HomeResourcesId=#{homeresourse}")
    void updatehomerecourse(String homeresourse, String url);

    @Update("update videopending set VideoStatus=#{i} where EnCode=#{encode}")
    void updateAuditStatush(String encode, int i);


    @Update("update video set VideoImageUrl= #{newCoverUrl} where VideoId= #{videoId}")
    void updateVideoCover(String videoId, String newCoverUrl);

    @Update("update video set VideoTime= #{duration} where VideoId= #{videoId}")
    void updateVideoDuration(String videoId, long duration);
}

