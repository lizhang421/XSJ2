package com.example.XSJ.Mapper.UserMapper;

import com.example.XSJ.Pojo.User.*;
import com.example.XSJ.Pojo.UserDTO.UserBaseDate;
import com.example.XSJ.Pojo.UserDTO.UserFollowDTO;
import com.example.XSJ.Pojo.UserDTO.UserMessageDTO;
import com.example.XSJ.Pojo.Video.*;
import com.example.XSJ.Pojo.VideoDTo.VideoTagDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("SELECT COUNT(*) FROM user WHERE Phone = #{phone}")
    int IsExitAmount(String phone);

    @Select("select count(*) from user where UserId=#{userid}")
    Integer IsExitId(String userid);

    @Insert("insert into user (UserId,EnCode,Phone,Password,RealName,NickName,CreatTime,UpdateTime,RegisterIp) values (#{userid},#{encode},#{phone}," +
            "#{password1},#{encode},#{encode},now(),now(),#{realIp})")
    void insertuser(String userid, String encode, String phone, String password1, String realIp);

    @Select("select PassWord from user where Phone=#{phone}")
    String GetPasswordByPhone(String phone);

    @Select("select * from user where phone=#{phone}")
    UserBaseDate GetUserBaseByPhone(String phone);

    @Select("select * from user where EnCode=#{encode}")
    UserMessageDTO GetUserMessageByEncode(String encode);

    int updateUserByDynamicFields(
            @Param("encode") String enCode,
            @Param("filteredData") Map<String, Object> filteredData);

    @Select("select NickName from user where UserId=#{publisherId}")
    String getUserNameById(String publisherId);


    @Select({
            "SELECT ",
            // user 表字段（你之前给的实体全部映射）
            "u.RealName      AS realName,",
            "u.NickName      AS nickName,",
            "u.Phone         AS phone,",
            "u.EnCode        AS enCode,",
            "u.Email         AS email,",
            "u.Avatar        AS avatar,",
            "u.Gender        AS gender,",
            "u.BirthDay      AS birthDay,",
            "u.RoleBase      AS roleBase,",
            "u.UiMember      AS uiMember,",
            "u.Status AS Status,",
            "u.Leavel        AS leavel,",
            "u.Address       AS address,",
            "u.Country       AS country,",
            "u.School        AS school,",
            "u.Language      AS language,",
            "u.Age           AS age,",
            "u.BackImage     AS backimage,",
            "ac.AccountStatuId      AS accountStatusId,",
            "ac.AccountStatusType   AS accountStatusType,",
            "ac.StartTime           AS startTime,",
            "ac.EndTime             AS endTime,",
            "ac.Reason              AS reason,",
            "ac.CreateTime           AS accountCreateTime ",

            "FROM `user` u ",
            "LEFT JOIN accountstatus ac ON ac.UserId = u.UserId ",
            "WHERE u.EnCode = #{enCode}"
    })
    List<UserMessage> GetUserDataByEncode(@Param("enCode") String enCode);


    @Update("UPDATE user SET Language = #{language} WHERE EnCode = #{encode}")
    int updateuserlanguage(String encode, String language);


    @Select({
            "SELECT ",
            "u.NickName     AS nickName, ",
            "u.EnCode       AS enCode, ",
            "u.Email        AS email, ",
            "u.Avatar       AS avatar, ",
            "u.Gender       AS gender, ",
            "u.School       AS school, ",
            "uf.StartTime   AS startTime, ",

            // 修正：视频发布者 = PublisherId
            "(SELECT COUNT(*) FROM video v WHERE v.PublisherId = u.UserId) AS videoMount, ",

            // 粉丝数统计（保持正确）
            "(SELECT COUNT(*) FROM userfollow uf2 WHERE uf2.FollowedUserId = u.UserId AND uf2.Status = 1) AS fansMount ",

            "FROM `user` u ",
            "LEFT JOIN userfollow uf ON u.UserId = uf.FollowedUserId ",
            "WHERE uf.UserId = (SELECT userId FROM `user` WHERE EnCode = #{enCode}) ",
            "AND uf.Status = 1"
    })
    List<UserFollowDTO> getfinduserfollow(@Param("enCode") String enCode);

    @Select({
            "SELECT ",
            "v.EnCode             AS enCode, ",
            "v.VideoId            AS videoId, ",
            "v.VideoName          AS videoName, ",
            "v.VideoImageUrl      AS videoImageUrl, ",
            "v.VideoUrl           AS videoUrl, ",
            "u.NickName           AS publisherName, ",
            "v.VideoTime          AS videoTime, ",
            "v.ViewCount          AS viewCount, ",
            "v.LikeCount          AS likeCount, ",
            "v.CommentCount       AS commentCount, ",
            "v.ShareCount         AS shareCount, ",
            "v.CollectCount       AS collectCount, ",
            "v.IsFeature          AS isFeature, ",
            "v.VideoResolution    AS videoResolution, ",
            "v.IsCollection       AS isCollection, ",
            "v.VideoSort          AS videoSort, ",
            "v.IsDelete           AS isDelete, ",
            "v.VideoStatus        AS videoStatus, ",
            "v.CreateTime         AS createTime, ",
            "v.Introduction       as introduction ",

            "FROM video v ",
            "LEFT JOIN `user` u ON u.UserId = v.PublisherId ",
            "WHERE u.EnCode = #{enCode} ",
            "AND v.IsDelete = 0 ",
            "AND v.VideoStatus = 0"
    })
    List<VideoTagDTO> getfinduserpost(@Param("enCode") String enCode);

    @Select({
            "SELECT ",
            "v.EnCode             AS enCode, ",
            "v.VideoName          AS videoName, ",
            "v.VideoImageUrl      AS videoImageUrl, ",
            "v.VideoUrl           AS videoUrl, ",
            "pu.NickName          AS publisherName, ",

            "v.VideoTime          AS videoTime, ",
            "v.ViewCount          AS viewCount, ",
            "v.LikeCount          AS likeCount, ",
            "v.CommentCount       AS commentCount, ",
            "v.ShareCount         AS shareCount, ",
            "v.CollectCount       AS collectCount, ",
            "v.IsFeature          AS isFeature, ",
            "v.VideoResolution    AS videoResolution, ",
            "v.IsCollection       AS isCollection, ",
            "v.VideoSort          AS videoSort, ",
            "v.IsDelete           AS isDelete, ",
            "v.VideoStatus        AS videoStatus, ",
            "v.CreateTime         AS createTime, ",
            "v.Introduction       AS introduction ",
            "FROM `user` u ",
            "LEFT JOIN uservideointeract ui ON ui.UserId = u.UserId ",
            "LEFT JOIN video v ON ui.VideoId = v.VideoId ",
            "LEFT JOIN `user` pu ON v.PublisherId = pu.UserId ",
            "WHERE u.EnCode = #{enCode} ",
            "AND ui.isCollect = 1 ",
            "AND v.IsDelete = 0 ",
            "AND v.VideoStatus = 0"
    })
    List<VideoTagDTO> getuservollect(@Param("enCode") String enCode);


    @Select({
            "SELECT ",
            "v.EnCode             AS enCode, ",
            "v.VideoName          AS videoName, ",
            "v.VideoImageUrl      AS videoImageUrl, ",
            "v.VideoUrl           AS videoUrl, ",
            "pu.NickName          AS publisherName, ",

            "v.VideoTime          AS videoTime, ",
            "v.ViewCount          AS viewCount, ",
            "v.LikeCount          AS likeCount, ",
            "v.CommentCount       as commentCount, ",
            "v.ShareCount         as shareCount, ",
            "v.CollectCount       as collectCount, ",
            "v.IsFeature          as isFeature, ",
            "v.VideoResolution    as videoResolution, ",
            "v.IsCollection       as isCollection, ",
            "v.VideoSort          as videoSort, ",
            "v.IsDelete           as isDelete, ",
            "v.VideoStatus        as videoStatus, ",
            "v.CreateTime         as createTime, ",
            "v.Introduction       as introduction ",
            "FROM `user` u ",
            "LEFT JOIN uservideointeract ui ON ui.UserId = u.UserId ",
            "LEFT JOIN video v ON ui.VideoId = v.VideoId ",
            "LEFT JOIN `user` pu ON v.PublisherId = pu.UserId ",
            "WHERE u.EnCode = #{enCode} ",
            "AND ui.isLike = 1 ",  // 这里改成了点赞状态
            "AND v.IsDelete = 0 ",
            "AND v.VideoStatus = 0"
    })
    List<VideoTagDTO> getfinduserlike(@Param("enCode") String enCode);


    @Update({
            "<script>",
            "UPDATE userfollow uf",
            "INNER JOIN user u1 ON uf.UserId = u1.UserId",
            "INNER JOIN user u2 ON uf.FollowedUserId = u2.UserId",
            "SET uf.Status = #{status},",
            "    uf.CreatTime = NOW(),",
            "    uf.StartTime = NOW()",
            "WHERE u1.EnCode = #{userEncode}",
            "  AND u2.EnCode = #{followedUserCode}",
            "</script>"
    })
    int cancelFollow(@Param("userEncode") String userEncode,
                     @Param("followedUserCode") String followedUserCode,
                     @Param("status") int status);


    @Update("update user set UserTagWeight=#{interestJson} where UserId=#{userId}")
    void updateUserInterest(String userId, String interestJson);

    @Select("select UserId from user where EnCode=#{userEncode}")
    String getuserid(String userEncode);

    @Select({
            "SELECT ",
            "u.NickName      AS nickName,",
            "u.EnCode        AS enCode,",
            "u.Email         AS email,",
            "u.Avatar        AS avatar,",
            "u.Gender        AS gender,",
            "u.Leavel        AS leavel,",
            "u.Country       AS country,",
            "u.School        AS school,",
            "u.Age           AS age,",
            "u.BackImage     AS backImage ",

            "FROM `user` u ",
            "WHERE u.EnCode = #{enCode}"
    })
    Oothermessge GetOtherMessageDate(@Param("enCode") String enCode);


    @Select("SELECT COUNT(*) FROM userfollow uf " +
            "INNER JOIN user u1 ON uf.UserId = u1.UserId " +
            "INNER JOIN user u2 ON uf.FollowedUserId = u2.UserId " +
            "WHERE u1.EnCode = #{userEncode} AND u2.EnCode = #{enCode}")
    int checkFollowExist(
            @Param("userEncode") String userEncode,
            @Param("enCode") String enCode
    );

    @Select("""
                SELECT
                    ui.id AS id,
                    u.EnCode AS userEncode,
                    v.EnCode AS videoEncode,
            
                    ui.isLike AS isLike,
                    ui.isCollect AS isCollect,
                    ui.isComment AS isComment,
                    ui.isShare AS isShare,
            
                    v.VideoName AS videoName,
                    v.VideoImageUrl AS videoImageUrl,
                    v.VideoUrl AS videoUrl,
            
                    author.EnCode AS authorEncode,
                    author.NickName AS authorName,
            
                    ui.playPercent AS playPercent,
                    ui.lastPosition AS lastPosition,
                    ui.interactScore AS interactScore,
                    ui.createTime AS createTime,
                    ui.updateTime AS updateTime,
                    ui.isRead AS isRead
            
                FROM uservideointeract ui
            
                JOIN video v 
                    ON ui.videoId = v.VideoId
                    AND v.VideoStatus = 0
                    AND v.IsDelete = 0
            
                LEFT JOIN user u 
                    ON ui.userId = u.UserId
            
                LEFT JOIN user author
                    ON v.PublisherId = author.UserId
            
                WHERE
                    u.EnCode = #{userEncode}
                    AND (ui.isLike = 1 OR ui.isCollect = 1 OR ui.isComment = 1 OR ui.isShare = 1)
            
                ORDER BY ui.updateTime DESC
            """)
    List<UserInteractDetailVO> FindUserInterest(@Param("userEncode") String userEncode);

    @Select("""
                SELECT
                    ui.id AS id,
                    u.EnCode AS userEncode,
                    v.EnCode AS videoEncode,
            
                    ui.isLike AS isLike,
                    ui.isCollect AS isCollect,
                    ui.isComment AS isComment,
                    ui.isShare AS isShare,
            
                    v.VideoName AS videoName,
                    v.VideoImageUrl AS videoImageUrl,
                    v.VideoUrl AS videoUrl,
            
                    author.EnCode AS authorEncode,
                    author.NickName AS authorName,
            
                    ui.playPercent AS playPercent,
                    ui.lastPosition AS lastPosition,
                    ui.interactScore AS interactScore,
                    ui.createTime AS createTime,
                    ui.updateTime AS updateTime,
                    ui.isRead AS isRead
            
                FROM uservideointeract ui
            
                JOIN video v 
                    ON ui.videoId = v.VideoId
                    AND v.VideoStatus = 0
                    AND v.IsDelete = 0
            
                LEFT JOIN user u 
                    ON ui.userId = u.UserId
            
                LEFT JOIN user author
                    ON v.PublisherId = author.UserId
            
                WHERE
                    author.EnCode = #{userEncode}
                    AND ui.userId != author.UserId
                    AND (ui.isLike = 1 OR ui.isCollect = 1 OR ui.isComment = 1 OR ui.isShare = 1)
            
                ORDER BY ui.updateTime DESC
            """)
    List<UserInteractDetailVO> FindOtherUserInterest(@Param("userEncode") String userEncode);

    @Update("<script>"
            + "UPDATE user SET "
            + "realName = COALESCE(#{userUpdateDTO.realName}, realName), "
            + "nickName = COALESCE(#{userUpdateDTO.nickName}, nickName), "
            + "phone = COALESCE(#{userUpdateDTO.phone}, phone), "
            + "email = COALESCE(#{userUpdateDTO.email}, email), "
            + "gender = COALESCE(#{userUpdateDTO.gender}, gender), "

            + "birthday = CASE WHEN #{userUpdateDTO.birthday} IS NOT NULL AND #{userUpdateDTO.birthday} != '' THEN #{userUpdateDTO.birthday} ELSE birthday END, "

            + "school = COALESCE(#{userUpdateDTO.school}, school), "
            + "leavel = COALESCE(#{userUpdateDTO.leavel}, leavel), "
            + "address = COALESCE(#{userUpdateDTO.address}, address), "
            + "country = COALESCE(#{userUpdateDTO.country}, country), "
            + "language = COALESCE(#{userUpdateDTO.language}, language), "
            + "age = COALESCE(#{userUpdateDTO.age}, age), "
            + "updateTime = NOW() "
            + "WHERE enCode = #{userEncode}"
            + "</script>")
    void UpdateUserInfoMessage(
            @Param("userEncode") String userEncode,
            @Param("userUpdateDTO") UserUpdateDTO userUpdateDTO
    );





    @Select("select * from videotype")
    List<VideoType> getalltype();


    @Select("SELECT vp.*, u.nickName, u.avatar FROM videopending vp " +
            "LEFT JOIN `user` u ON u.UserId = vp.PublisherId " +
            "WHERE u.EnCode = #{encode}" +
            "AND vp.IsDelete= 0")
    List<VideoPending> FindUserVideoAudit(String encode);


    @Insert("INSERT INTO userreview " +
            "(id, UserId, TargetType, Status, Content, IsRead, CreateTime, UpdateTime, RecourseUrl) " +
            "VALUES " +
            "(REPLACE(UUID(), '-', ''), #{userid}, #{avatar}, 0, #{avatar}, 0, NOW(), NOW(), #{avatarURL})")
    void addUserAvater(
            @Param("userid") String userid,
            @Param("avatarURL") String avatarURL,
            @Param("avatar") String avatar
    );


    @Select("SELECT COUNT(*) FROM userreview WHERE UserId = #{userid} AND TargetType = 'AVATAR' AND Status = 0")
    int getAvatarPending(String userid);

    @Select("SELECT COUNT(*) FROM userreview WHERE UserId = #{userid} AND TargetType = 'BACKGROUND' AND Status = 0")
    int getBackImagePending(String userid);


    @Insert("INSERT INTO Report (Id,CreateUserId,TargetUserId,ReportType,TargetId,Reason,Remark,Status,UserIsRead,TargetUserIsRead,CreateTime,UpdateTime) " +
            "VALUES (" +
            "#{reportId}," +
            "(SELECT UserId FROM User WHERE EnCode = #{userEncode})," +
            "CASE WHEN #{reportType}=1 THEN (SELECT UserId FROM User WHERE EnCode = #{targetUserEncode}) ELSE #{targetUserEncode} END," +
            "#{reportType}," +
            "CASE WHEN #{reportType}=1 THEN (SELECT VideoId FROM Video WHERE EnCode = #{targetEncode}) ELSE #{targetEncode} END," +
            "#{reason},#{remark},3,1,0,NOW(),NOW())"
    )
    int insertreport(
            @Param("reportId") String reportId,
            @Param("userEncode") String userEncode,
            @Param("targetUserEncode") String targetUserEncode,
            @Param("reportType") int reportType,
            @Param("targetEncode") String targetEncode,
            @Param("reason") String reason,
            @Param("remark") String remark
    );



    @Select({
            "SELECT r.*,",
            "v.EnCode,",
            "v.VideoName,",
            "v.VideoImageUrl,",
            "c.Content commentContent ",
            "FROM Report r ",
            "LEFT JOIN Video v ON r.ReportType = 1 AND r.TargetId = v.VideoId ",
            "LEFT JOIN VideoComment c ON r.ReportType = 2 AND r.TargetId = c.VideoCommentId ",
            "WHERE r.CreateUserId = #{userId} ",
            "ORDER BY r.CreateTime DESC"
    })
    List<ReportVO> getreport(@Param("userId") String userId);


    @Select({
            "SELECT r.*,",
            "v.EnCode,",
            "v.VideoName,",
            "v.VideoImageUrl,",
            "c.Content AS commentContent ",
            "FROM Report r ",
            "LEFT JOIN Video v ON r.ReportType = 1 AND r.TargetId = v.VideoId ",
            "LEFT JOIN VideoComment c ON r.ReportType = 2 AND r.TargetId = c.VideoCommentId ",
            "WHERE r.TargetUserId = #{userId} ",
            "AND r.Status = 1 ",
            "ORDER BY r.CreateTime DESC"
    })
    List<ReportVO> getgetreport(@Param("userId") String userId);


    /**
     * 批量标记举报为已读
     * 自动判断：举报人更新 UserIsRead，被举报人更新 TargetUserIsRead
     */
    @Update({
            "<script>",
            "UPDATE Report ",
            "<set>",
            "    UserIsRead = CASE WHEN CreateUserId = #{userId} THEN 1 ELSE UserIsRead END,",
            "    TargetUserIsRead = CASE WHEN TargetUserId = #{userId} THEN 1 ELSE TargetUserIsRead END",
            "</set>",
            "WHERE Id IN ",
            "<foreach collection='reportId' open='(' separator=',' close=')' item='id'>",
            "    #{id}",
            "</foreach>",
            "</script>"
    })
    int readreport(@Param("reportId") List<String> reportId, @Param("userId") String userId);

    @Select("SELECT PassWord FROM User WHERE EnCode = #{userEncode}")
    String getPassword(String userEncode);

    @Update("UPDATE User SET PassWord = #{newpassword} WHERE EnCode = #{userEncode}")
    int updatePassword(String userEncode, String newpassword);

    @Select("SELECT " +
            "COUNT(DISTINCT UserId) AS totalUsers, " +
            "SUM(CASE WHEN DATE(CreatTime) = CURDATE() AND DeleteMark = 0 THEN 1 ELSE 0 END) AS todayUsers, " +
            "ROUND(" +
            "  (SUM(CASE WHEN DATE(CreatTime) = CURDATE() AND DeleteMark = 0 THEN 1 ELSE 0 END) " +
            "  - SUM(CASE WHEN DATE(CreatTime) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) AND DeleteMark = 0 THEN 1 ELSE 0 END)) " +
            "  * 100.0 / NULLIF(SUM(CASE WHEN DATE(CreatTime) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) AND DeleteMark = 0 THEN 1 ELSE 0 END), 0), " +
            "2) AS todayUserGrowth, " +
            "ROUND(" +
            "  ( (SUM(CASE WHEN CreatTime >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND CreatTime < CURDATE() AND DeleteMark = 0 THEN 1 ELSE 0 END) / 7) " +
            "  - (SUM(CASE WHEN CreatTime >= DATE_SUB(CURDATE(), INTERVAL 14 DAY) AND CreatTime < DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND DeleteMark = 0 THEN 1 ELSE 0 END) / 7) ) " +
            "  * 100.0 / NULLIF(SUM(CASE WHEN CreatTime >= DATE_SUB(CURDATE(), INTERVAL 14 DAY) AND CreatTime < DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND DeleteMark = 0 THEN 1 ELSE 0 END) / 7, 0), " +
            "2) AS userGrowth " +
            "FROM user " +
            "WHERE DeleteMark = 0")
    Map<String, Object> getUserDashboardStats();

    @Select("SELECT " +
            "SUM(CASE WHEN DATE(CreatTime) = DATE_SUB(CURDATE(), INTERVAL 6 DAY) THEN 1 ELSE 0 END) AS day1, " +
            "SUM(CASE WHEN DATE(CreatTime) = DATE_SUB(CURDATE(), INTERVAL 5 DAY) THEN 1 ELSE 0 END) AS day2, " +
            "SUM(CASE WHEN DATE(CreatTime) = DATE_SUB(CURDATE(), INTERVAL 4 DAY) THEN 1 ELSE 0 END) AS day3, " +
            "SUM(CASE WHEN DATE(CreatTime) = DATE_SUB(CURDATE(), INTERVAL 3 DAY) THEN 1 ELSE 0 END) AS day4, " +
            "SUM(CASE WHEN DATE(CreatTime) = DATE_SUB(CURDATE(), INTERVAL 2 DAY) THEN 1 ELSE 0 END) AS day5, " +
            "SUM(CASE WHEN DATE(CreatTime) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) THEN 1 ELSE 0 END) AS day6, " +
            "SUM(CASE WHEN DATE(CreatTime) = CURDATE() THEN 1 ELSE 0 END) AS day7 " +
            "FROM `user` WHERE DeleteMark = 0")
    Map<String, Object> getUserWeekGrowth();

    @Select("SELECT " +
            "COUNT(DISTINCT UserId) AS totalUsers, " +
            "SUM(CASE WHEN DATE(CreatTime) = CURDATE() AND DeleteMark = 0 THEN 1 ELSE 0 END) AS todayUsers, " +
            "ROUND((SUM(CASE WHEN DATE(CreatTime) = CURDATE() THEN 1 ELSE 0 END) - " +
            "SUM(CASE WHEN DATE(CreatTime) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) THEN 1 ELSE 0 END)) * 100.0 " +
            "/ NULLIF(SUM(CASE WHEN DATE(CreatTime) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) THEN 1 ELSE 0 END), 0), 2) AS todayUserGrowth, " +
            "ROUND((SUM(CASE WHEN CreatTime >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN 1 ELSE 0 END) / 7 - " +
            "SUM(CASE WHEN CreatTime >= DATE_SUB(CURDATE(), INTERVAL 14 DAY) AND CreatTime < DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN 1 ELSE 0 END) / 7) * 100.0 " +
            "/ NULLIF(SUM(CASE WHEN CreatTime >= DATE_SUB(CURDATE(), INTERVAL 14 DAY) AND CreatTime < DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN 1 ELSE 0 END) / 7, 0), 2) AS userGrowth " +
            "FROM `user` WHERE DeleteMark = 0")
    Map<String, Object> getUserStats();
    // 学历统计（真实数据库）
    @Select("SELECT Leavel AS name, COUNT(*) AS value " +
            "FROM user " +
            "WHERE Leavel IS NOT NULL AND Leavel != '' " +
            "GROUP BY Leavel " +
            "ORDER BY value DESC")
    List<Map<String, Object>> getEducationDistribution();

    // 语言统计（真实数据库）
    @Select("SELECT Language AS name, COUNT(*) AS value " +
            "FROM user " +
            "WHERE Language IS NOT NULL AND Language != '' " +
            "GROUP BY Language " +
            "ORDER BY value DESC")
    List<Map<String, Object>> getLanguageDistribution();



    @Select("SELECT DATE_FORMAT(CreatTime, '%Y-%m') AS month, COUNT(*) AS num " +
            "FROM user GROUP BY month ORDER BY month")
    List<Map<String, Object>> getUserMonthTrend();

    @Select("SELECT DATE_FORMAT(CreateTime, '%Y-%m') AS month, COUNT(*) AS num " +
            "FROM video WHERE IsDelete = 0 GROUP BY month ORDER BY month")
    List<Map<String, Object>> getVideoMonthTrend();

    @Select("SELECT DATE_FORMAT(CreateTime, '%Y-%m') AS month, IFNULL(SUM(ViewCount), 0) AS num " +
            "FROM video WHERE IsDelete = 0 GROUP BY month ORDER BY month")
    List<Map<String, Object>> getViewMonthTrend();


    @Select("SELECT " +
            "    AiId AS name, " +
            "    COUNT(*) AS value " +
            "FROM userandai " +
            "WHERE IsDeleted = 1 " +  // 这里改成 1 ！！！
            "GROUP BY AiId " +
            "ORDER BY value DESC")
    List<Map<String, Object>> getAiUsageDistribution();

    @Select("SELECT COUNT(*) FROM friendrelation fr " +
            "INNER JOIN user u1 ON fr.UserId = u1.UserId " +
            "INNER JOIN user u2 ON fr.FriendId = u2.UserId " +
            "WHERE (u1.EnCode = #{encodeA} AND u2.EnCode = #{encodeB} AND fr.Status = 1) " +
            "OR (u1.EnCode = #{encodeB} AND u2.EnCode = #{encodeA} AND fr.Status = 1)")
    int isFriendByEncode(@Param("encodeA") String encodeA, @Param("encodeB") String encodeB);



    @Insert("INSERT INTO userfollow (UserFollowId, UserId, FollowedUserId, Status, StartTime, EndTime, CreatTime) " +
            "SELECT " +
            "UUID(), " +
            "u1.UserId, " +
            "u2.UserId, " +
            "#{status}, " +
            "NOW(), " +
            "NULL, " +
            "NOW() " +
            "FROM user u1 " +
            "CROSS JOIN user u2 " +
            "WHERE u1.EnCode = #{userEncode} AND u2.EnCode = #{enCode}")
    void insertFollow(@Param("userEncode") String userEncode,
                      @Param("enCode") String enCode,
                      @Param("status") int status);

    @Select({
            "SELECT COUNT(*) ",
            "FROM userfollow uf ",
            "INNER JOIN user u1 ON uf.UserId = u1.UserId ",
            "INNER JOIN user u2 ON uf.FollowedUserId = u2.UserId ",
            "WHERE ",
            "  u1.EnCode = #{encode} ",
            "  AND u2.EnCode = #{userEncode} ",
            "  AND uf.Status = 1"
    })
    Integer checkIsFollowed(
            @Param("encode") String encode,
            @Param("userEncode") String userEncode
    );


    @Select("SELECT " +
            "RealName AS realName, " +
            "NickName AS nickName, " +
            "Phone AS phone, " +
            "EnCode AS enCode, " +
            "Email AS email, " +
            "Avatar AS avatar, " +
            "Gender AS gender, " +
            "BirthDay AS birthDay, " +
            "RoleBase AS roleBase, " +
            "UiMember AS uiMember, " +
            "CreatTime AS createTime, " +  //
            "Leavel AS leavel, " +
            "Address AS address, " +
            "Country AS country, " +
            "School AS school, " +
            "Language AS language, " +
            "Age AS age, " +
            "Status AS status " +
            "FROM user " +
            "WHERE DeleteMark = 0 " +
            "AND NickName LIKE CONCAT('%', #{key}, '%') " +
            "ORDER BY CreatTime DESC")  //
    List<UserMessageDTO> FindUserMessageByCode(@Param("key") String key);

    @Insert("insert into user(UserId,Phone,PassWord,NickName,RealName,RoleBase,EnCode) values(#{id},#{phone},#{password},#{nickname},#{realname},1,#{encode})")
    int insertmanager(String id, String phone, String password, String nickname, String realname, String encode);

    @Select("select count(1) from user where Phone = #{phone}")
    int checkphone(String phone);
}
