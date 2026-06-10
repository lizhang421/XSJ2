package com.example.XSJ.Mapper.Ai;

import com.example.XSJ.Pojo.Ai.AiConfigFrontDTO;
import com.example.XSJ.Pojo.Ai.UserAndAiDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AiMapper {

    @Select("select AiTone from user where EnCode=#{userEncode}")
    int getuserhone(String userEncode);

    @Select("update user set AiTone=#{hone} where EnCode=#{userEncode}")
    void updateuserhone(String userEncode, int hone);


    @Select("select * from aiconfig  where Status=1")
    List<AiConfigFrontDTO> getAiModel();


    @Select("""
        SELECT  ua.*
        FROM  userandai ua
        LEFT JOIN `user` u ON u.UserId = ua.UserId
        WHERE u.EnCode = #{encode}
          AND ua.IsDeleted = 1
        """) // 多行SQL用三重引号，更易读
    List<UserAndAiDTO> getConversationList(@Param("encode") String encode);
}
