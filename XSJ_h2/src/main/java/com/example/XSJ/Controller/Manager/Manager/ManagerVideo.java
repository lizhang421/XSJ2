package com.example.XSJ.Controller.Manager.Manager;


import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.Video.DeleteVideoDTO;
import com.example.XSJ.ServiceImpl.Manager.Manager.ManagerVideoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/Video")
public class ManagerVideo {

    @Autowired
    private ManagerVideoServiceImpl managerVideoServiceimpl;


    /**
     * 管理员获取视频 全部的视频，然后对视频进行查 删
     */
    @RequestMapping("/getVideo")
    public Result getVideo( int pageNum, int pageSize,int status){
        try {
            Result result = managerVideoServiceimpl.getVideo(pageNum,pageSize,status);
            return result;
        }catch (Exception e){
            log.error("获取视频失败",e);
            return Result.error();
        }
    }

    /**
     * 根据视频的encode 查询视频的详细信息，如果视频是审核经过的要加入很多详细信息，是审核中的就只查询审核表的信息
     */
    @RequestMapping("/getVideoDetail")
    public Result getVideoDetail(String encode,int where){
        try {
            Result result = managerVideoServiceimpl.getVideoDetail(encode,where);
            return result;
        }catch (Exception e){
            log.error("获取视频详情失败",e);
            return Result.error();
        }
    }

    /**
     * 管理员紧急下架封禁视频
     */
    @PostMapping("/deleteVideo")
    public Result deleteVideo(
            @RequestBody DeleteVideoDTO dto
    ){
        try {
          String  encode=dto.getEncode();
          List<String> videoAuditTagId=dto.getVideoAuditTagId();
          int status=dto.getStatus();
            Result result = managerVideoServiceimpl.deleteVideo(encode,videoAuditTagId,status);
            return result;
        }catch (Exception e){
            log.error("删除视频失败",e);
            return Result.error();
        }
    }








}
