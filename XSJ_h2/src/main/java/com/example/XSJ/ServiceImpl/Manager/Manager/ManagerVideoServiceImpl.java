package com.example.XSJ.ServiceImpl.Manager.Manager;

import com.example.XSJ.Pojo.Result;

import java.util.List;

public interface ManagerVideoServiceImpl {


    Result getVideo(int pageNum, int pageSize, int status);

    Result getVideoDetail(String encode, int where);

    Result deleteVideo(String encode, List<String> videoAuditTagId, int status);
}
