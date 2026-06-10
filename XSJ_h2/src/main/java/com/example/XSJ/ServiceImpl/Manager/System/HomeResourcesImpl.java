package com.example.XSJ.ServiceImpl.Manager.System;

import com.example.XSJ.Pojo.System.HomeResources;
import com.example.XSJ.Pojo.VideoDTo.HomeResourcesDTO;

import java.util.List;

public interface HomeResourcesImpl {
    List<HomeResources> selectAllEnableResourse(Integer isUser);

    List<HomeResourcesDTO> getHoneTop(Integer isUse, String recourseAddress);
}
