package com.example.XSJ.Mapper.System;


import com.example.XSJ.Pojo.System.HomeResources;
import com.example.XSJ.Pojo.VideoDTo.HomeResourcesDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HomeResourseMapper {
    /**
     * 查询首页启动资源，后续有其他启动资源可以放在这里
     * @param isUser
     * @return
     */
    @Select("SELECT * FROM HomeResources WHERE IsUse = #{isUse}")
    List<HomeResources> selectAllEnableResourse(Integer isUser);

    // 修正方法名拼写 + 规范SQL
    @Select("SELECT * FROM HomeResources WHERE IsUse = #{isUse} AND ResourceAddress = #{resourceAddress}")
    List<HomeResourcesDTO> getHomeTop(Integer isUse, String resourceAddress); // 修正Hone→Home
}


