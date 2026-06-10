package com.example.XSJ.service.Manager.Manager;

import com.example.XSJ.Mapper.System.ManagerCommentMapper;
import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.Video.VideoCommentManager;
import com.example.XSJ.ServiceImpl.Manager.Manager.ManagerCommentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ManagerMapperService implements ManagerCommentServiceImpl {
    @Autowired
    private ManagerCommentMapper managerCommentMapper;


    @Override
    public Result getCommentList(int pageNum, int pageSize, Integer status, String key, LocalDateTime startTime, LocalDateTime endTime) {

        int offset = (pageNum - 1) * pageSize;


        try {
            List<VideoCommentManager> videoCommentManagerList =
                    managerCommentMapper.getCommentList(offset, pageSize, status, key, startTime, endTime);


            int total = managerCommentMapper.getCommentCount(status, key, startTime, endTime);

            Map<String, Object> map = new HashMap<>();
            map.put("list", videoCommentManagerList);
            map.put("total", total);
            map.put("pageNum", pageNum);
            map.put("pageSize", pageSize);

            return Result.success(map);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.error("获取用户列表失败");
        }

    }

    @Override
    public Result deleteComment(String commentId, Integer status) {

        try {
            int result = managerCommentMapper.deleteComment(commentId, status);
            return result > 0 ? Result.success() : Result.error("删除失败");

        } catch (Exception e) {
            log.error("删除评论失败", e);
            return Result.error("删除评论失败");
        }
    }


}



