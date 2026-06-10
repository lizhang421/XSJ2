package com.example.XSJ.Controller.Manager.Manager;


import cn.hutool.core.date.DateTime;
import cn.hutool.log.Log;
import com.example.XSJ.Pojo.Result;
import com.example.XSJ.ServiceImpl.Manager.Manager.ManagerCommentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/admin/comment")
public class ManagerComment  {

    @Autowired
    private ManagerCommentServiceImpl managerCommentServiceimpl;


    /**
     * 获取评论列表
     */
    @RequestMapping("/getCommentList")
    public Result getCommentList(int pageNum, int pageSize, Integer status, String key, LocalDateTime startTime, LocalDateTime  endTime){
        try {
            Result result = managerCommentServiceimpl.getCommentList(pageNum,pageSize,status,key,startTime,endTime);
            return result;
        }catch (Exception e){
            log.error("获取评论列表失败",e);
            return Result.error();
        }
    }

    @RequestMapping("/deleteComment")
    public Result deleteComment(String commentId, Integer status){
        try {
            Result result = managerCommentServiceimpl.deleteComment(commentId, status);
            return result;
        }catch (Exception e){
            log.error("删除评论失败",e);
            return Result.error();
        }
    }









}
