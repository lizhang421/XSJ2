package com.example.XSJ.Mapper.Ai;

import com.example.XSJ.Pojo.Ai.AiConfig;
import com.example.XSJ.Pojo.Ai.UserAndAi;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AiConfigMapper {

    @Select("SELECT * FROM aiconfig WHERE ModelCode = #{modelCode} AND Status = 1 LIMIT 1")
    AiConfig selectByModelCode(String modelCode);

    @Select("SELECT * FROM aiconfig WHERE Status = 1 LIMIT 1")
    AiConfig selectDefaultEnableConfig();




    /**
     * 插入新会话
     */
    @Insert("INSERT INTO UserAndAi (UserAndAi, UserId, ConvTitle, CreatTime, UpdateTime, IsDeleted, AiId) " +
            "VALUES (#{userAndAi}, #{userId}, #{convTitle}, #{creatTime}, #{updateTime}, #{isDeleted}, #{aiId})")
    int insert(UserAndAi userAndAi);

    /**
     * 根据主键查询会话
     */
    @Select("SELECT UserAndAi as userAndAi, UserId as userId, ConvTitle as convTitle, " +
            "CreatTime as creatTime, UpdateTime as updateTime, IsDeleted as isDeleted, AiId as aiId " +
            "FROM UserAndAi WHERE UserAndAi = #{userAndAi}")
    UserAndAi selectById(String userAndAi);

    /**
     * 根据用户ID查询会话列表（未删除）
     */
    @Select("SELECT UserAndAi as userAndAi, UserId as userId, ConvTitle as convTitle, " +
            "CreatTime as creatTime, UpdateTime as updateTime, IsDeleted as isDeleted, AiId as aiId " +
            "FROM UserAndAi WHERE UserId = #{userId} AND IsDeleted = 1 " +
            "ORDER BY UpdateTime DESC")
    List<UserAndAi> selectByUserId(String userId);

    /**
     * 根据用户ID和AI ID查询会话列表
     */
    @Select("SELECT UserAndAi as userAndAi, UserId as userId, ConvTitle as convTitle, " +
            "CreatTime as creatTime, UpdateTime as updateTime, IsDeleted as isDeleted, AiId as aiId " +
            "FROM UserAndAi WHERE UserId = #{userId} AND AiId = #{aiId} AND IsDeleted = 1 " +
            "ORDER BY UpdateTime DESC")
    List<UserAndAi> selectByUserIdAndAiId(@Param("userId") String userId, @Param("aiId") String aiId);

    /**
     * 更新会话标题
     */
    @Update("UPDATE UserAndAi SET ConvTitle = #{convTitle}, UpdateTime = CURTIME() " +
            "WHERE UserAndAi = #{userAndAi}")
    int updateTitle(@Param("userAndAi") String userAndAi, @Param("convTitle") String convTitle);

    /**
     * 更新会话时间
     */
    @Update("UPDATE UserAndAi SET UpdateTime = CURTIME() WHERE UserAndAi = #{userAndAi}")
    int updateTime(String userAndAi);

    /**
     * 软删除会话
     */
    @Update("UPDATE UserAndAi SET IsDeleted = 0, UpdateTime = CURTIME() WHERE UserAndAi = #{userAndAi}")
    int deleteById(String userAndAi);

    /**
     * 检查会话是否存在
     */
    @Select("SELECT COUNT(*) FROM UserAndAi WHERE UserAndAi = #{userAndAi} AND IsDeleted = 1")
    int existsById(String userAndAi);


    @Select("SELECT UserId FROM User WHERE EnCode = #{enCode}")
    String getUserIdByEncode(String enCode);
}
