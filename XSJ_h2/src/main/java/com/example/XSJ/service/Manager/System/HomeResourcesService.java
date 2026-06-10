package com.example.XSJ.service.Manager.System;

import com.example.XSJ.Mapper.System.HomeResourseMapper;

import com.example.XSJ.Pojo.System.HomeResources;
import com.example.XSJ.Pojo.VideoDTo.HomeResourcesDTO;
import com.example.XSJ.ServiceImpl.Manager.System.HomeResourcesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeResourcesService implements HomeResourcesImpl {

    @Autowired
    private HomeResourseMapper homeResourseMapper;

    @Override
    public List<com.example.XSJ.Pojo.System.HomeResources> selectAllEnableResourse(Integer isUser) {
        return homeResourseMapper.selectAllEnableResourse(isUser);
    }

    @Override
    public List<HomeResourcesDTO> getHoneTop(Integer isUse, String recourseAddress) {
        return  homeResourseMapper.getHomeTop(isUse,recourseAddress);
    }


}
