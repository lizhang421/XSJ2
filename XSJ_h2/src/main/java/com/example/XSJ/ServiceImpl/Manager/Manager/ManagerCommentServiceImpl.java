package com.example.XSJ.ServiceImpl.Manager.Manager;

import com.example.XSJ.Pojo.Result;

import java.time.LocalDateTime;

public interface ManagerCommentServiceImpl {
    Result getCommentList(int pageNum, int pageSize, Integer status, String key, LocalDateTime startTime, LocalDateTime endTime);

    Result deleteComment(String commentId, Integer status);
}
