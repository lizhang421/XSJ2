package com.example.XSJ.Mapper.System;

import com.example.XSJ.Pojo.Ai.AddAiConfig;
import com.example.XSJ.Pojo.Ai.AiConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ManagerAiMapper {

    @Select({
            "<script>",
            "SELECT * FROM AiConfig",
            "<where>",
            "<if test='query.status != null'>AND Status = #{query.status}</if>",
            "<if test='query.aiConfigId != null and query.aiConfigId != \"\"'>AND AiConfigId = #{query.aiConfigId}</if>",
            "<if test='query.modelCode != null and query.modelCode != \"\"'>AND ModelCode = #{query.modelCode}</if>",
            "<if test='query.modelName != null and query.modelName != \"\"'>AND ModelName LIKE CONCAT('%', #{query.modelName}, '%')</if>",
            "<if test='query.introduction != null and query.introduction != \"\"'>AND Introduction LIKE CONCAT('%', #{query.introduction}, '%')</if>",
            "</where>",
            "LIMIT #{query.pageSize} OFFSET #{start}",
            "</script>"
    })
    List<AiConfig> selectAiConfigList(
            @Param("query") AiConfig query,
            @Param("start") int start
    );


    @Insert({
            "<script>",
            "INSERT INTO AiConfig(",
            "AiConfigId, ModelCode, ModelName, ApiKey, Introduction, ",
            "Status, BaseUrl, Model, Image, CreateTime, UpdateTime",
            ") VALUES (",
            "#{aiID}, ",
            "#{addAiConfig.modelCode}, ",
            "#{addAiConfig.modelName}, ",
            "#{addAiConfig.apiKey}, ",
            "#{addAiConfig.introduction}, ",
            "#{addAiConfig.status}, ",
            "#{addAiConfig.baseUrl}, ",
            "#{addAiConfig.model}, ",
            "#{addAiConfig.image}, ",
            "NOW(), ",  // 数据库自动生成时间
            "NOW()",    // 不用实体传值
            ")",
            "</script>"
    })
    void addAi(
            @Param("addAiConfig") AddAiConfig addAiConfig,
            @Param("aiID") String aiID
    );


    @Update({
            "<script>",
            "UPDATE AiConfig SET",
            "ModelCode = #{modelCode},",
            "ModelName = #{modelName},",
            "ApiKey = #{apiKey},",
            "BaseUrl = #{baseUrl},",
            "Model = #{model},",
            "Status = #{status},",
            "Image = #{image},",
            "Introduction = #{introduction},",
            "UpdateTime = NOW()",  // 自动更新时间
            "WHERE AiConfigId = #{aiConfigId}",  // ID 从实体中获取
            "</script>"
    })
    void updataai(AiConfig aiConfig);

    @Update("update AiConfig set Status=#{status} where AiConfigId=#{aiID}")
    void deleteAi(String aiID, Integer status);
}
