package com.example.XSJ.Mapper.UserMapper;

import com.example.XSJ.Pojo.User.UserVideoInteractDTO;
import com.example.XSJ.Pojo.Video.Video;
import com.example.XSJ.service.User.UserWatchHistoryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserHistoryServiceMapper {


    @Select({
            "SELECT DISTINCT uh.UserId ",
            "FROM userviewhistory uh ",
            "WHERE uh.watchTime >= DATE_SUB(NOW(), INTERVAL 3 DAY)"
    })
    List<String> queryrecentThreeDaysActiveUsers();


    @Select({
            // 主查询：展示需要的字段
            "SELECT ",
            "uh.UserId AS userId, ",
            "v.VideoId AS videoId, ",
            "vt.VideoTypeId AS videoTypeId, ",
            "vt.TypeName AS typeName, ",
            "uh.id AS id, ",
            "uh.watchTime AS watchTime ",

            // 第一步子查询固定取出20条唯一观看历史
            "FROM ( ",
            "SELECT id, UserId, VideoId, watchTime ",
            "FROM userviewhistory ",
            "WHERE UserId = #{userId} ",
            "AND watchTime >= DATE_SUB(NOW(), INTERVAL 3 DAY) ",
            "ORDER BY watchTime DESC ",
            "LIMIT 20 ", // 这里保证只有20条唯一历史
            ") AS uh ",

            // 关联视频表
            "INNER JOIN video v ON uh.VideoId = v.VideoId ",

            // 关联视频类型中间表
            "INNER JOIN videoandtype vat ON v.VideoId = vat.VideoId ",

            // 关联视频类型表
            "INNER JOIN videotype vt ON vat.TypeId = vt.VideoTypeId ",

            // 过滤有效视频
            "WHERE v.VideoStatus = 0 AND v.IsDelete = 0 ",

            // 排序
            "ORDER BY uh.watchTime DESC "
    })
    List<UserWatchHistoryDTO> queryUserHistory(@Param("userId") String userId);


    @Select({
            "<script>",
            "SELECT ",
            "  userId, ",
            "  videoId, ",
            "  isLike, ",
            "  isCollect, ",
            "  isComment, ",
            "  isShare, ",
            "  playPercent, ",
            "  interactScore ",
            "FROM uservideointeract ",
            "WHERE userId = #{userId} ",
            "AND videoId IN ",
            "<foreach collection='videoIds' item='videoId' open='(' separator=',' close=')'>",
            "    #{videoId}",
            "</foreach>",
            "</script>"
    })
    List<UserVideoInteractDTO> queryUserInteract(
            @Param("userId") String userId,
            @Param("videoIds") List<String> videoIds
    );
}
