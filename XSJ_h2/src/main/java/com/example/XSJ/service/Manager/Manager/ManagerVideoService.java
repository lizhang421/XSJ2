package com.example.XSJ.service.Manager.Manager;


import com.example.XSJ.Mapper.System.ManagerMapper;
import com.example.XSJ.Mapper.System.ManagerVideoMapper;
import com.example.XSJ.Mapper.VideoMapper.VideoMapper;
import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.UserDTO.UserMessageDTO;
import com.example.XSJ.Pojo.UserDTO.Userandvideinterseat;
import com.example.XSJ.Pojo.Video.Video;
import com.example.XSJ.Pojo.Video.VideoAuditTag;
import com.example.XSJ.Pojo.Video.VideoType;
import com.example.XSJ.Pojo.Video.VideoVO;
import com.example.XSJ.Pojo.VideoDTo.VideoFileDTO;
import com.example.XSJ.Pojo.VideoDTo.VideoPendingDTO;
import com.example.XSJ.ServiceImpl.Manager.Manager.ManagerVideoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ManagerVideoService  implements ManagerVideoServiceImpl {

    @Autowired
    private ManagerVideoMapper managerVideoMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private ManagerMapper managerMapper;

    @Override
    public Result getVideo(int pageNum, int pageSize, int status) {
        Map<String,Object> map = new HashMap<>();

        int count = 0;
        int start = (pageNum-1)*pageSize;
        try {

            List<VideoVO> videoVOList = null;
            List<VideoPendingDTO> videoPendingDTOList = null;
            if (status == 1) {
                videoVOList = managerVideoMapper.getPostVideo(pageSize, start);
                count=managerVideoMapper.getPostVideoCount();
                map.put("videoVOList", videoVOList);
                map.put("count", count);
                return Result.success(map);
            } else if (status == 0) {
                videoPendingDTOList = managerVideoMapper.getVideoPendingList(pageSize, start);
                count=managerVideoMapper.getPostVideoPendingCount();
                map.put("count", count);
                map.put("videoPendingDTOList", videoPendingDTOList);
                return Result.success(map);
            }
            //审核表通过的视频都会出现在video中所以这里只需要获得没有通过的
            return Result.error();
        }catch (Exception e){
            log.error("获取视频失败",e);
            return Result.error();
        }


    }

    @Transactional
    public Result getVideoDetail(String encode, int where) {

        Map<String,Object> map = new HashMap<>();
       try {
           if (where == 1) {
               //是正式表的
               //根据encode拆查询视频西悉尼
               Video video = videoMapper.findVideoByCode(encode);
               if (video == null){
                   return Result.error("未查询到相关信息");
               }
             String  publicshId=video.getPublisherId();

               List<VideoAuditTag> videoAuditTagList = null;
               if (video.getVideoStatus() == 1) {
                   //这个特殊0 是正常的 不正常的视频要去查询封禁原因
                   videoAuditTagList = videoMapper.findVideoAuditTagByCode(encode);
               }
               //获取视频相关的文件
               List<VideoFileDTO> videoFileDTOList = videoMapper.findVideoFileByCode(encode);
               //获取视频类型信息
               List<VideoType> videoTypeList = videoMapper.findVideoTypeByCode(encode);
               //查找到发布人信息
               UserMessageDTO UserMessageDTO = videoMapper.findPublisherNameByCode(publicshId);
               //查找这个视频都谁点赞关注收藏了
               List<Userandvideinterseat> userMessageDTOList = videoMapper.findUserMessageDTOByCode(encode);
               map.put("videoFileDTOList", videoFileDTOList);
               map.put("videoTypeList", videoTypeList);
               map.put("UserMessageDTO", UserMessageDTO);
               map.put("userMessageDTOList", userMessageDTOList);
               map.put("videoAuditTagList", videoAuditTagList);
               return Result.success(map);
               //剩下的用户的详细信息就去用户查询个人信息中心去查找 这个视频的用户信息，以及这个视频的评论区信息
           }else if (where == 2){
               //是审核表的就只看他是不是违禁视频，不是违禁视频就不查询，
               //这里只需要查询违禁原因就行了
               List<VideoAuditTag> videoAuditTagList = videoMapper.findVideopendingAuditTagByCode(encode);
               //剩下的用户的详细信息就去用户查询个人信息中心去查找
               map.put("videoAuditTagList", videoAuditTagList);
               return Result.success(map);
           }
       }catch (Exception e){
        log.error("获取视频详情失败");
        return Result.error();
         }
         return Result.error();
    }

    @Override
    public Result deleteVideo(String encode, List<String> videoAuditTagId, int status) {
        try {
            Video video = videoMapper.findVideoByCode(encode);
            if (video == null){
                return Result.error("未查询到相关信息");
            }
            //如果状态是0 ，那么就回复这个视频
            if (status==0&&videoAuditTagId.isEmpty()){
                //回复视频
                managerMapper.updateReplyVideo(encode);
                managerMapper.updateAuditStatush(encode,1);
                return Result.success();

            }
            if (status==1) {

                //所有的删除都是软删除，这个时候将视频状态改为status
                //修改video表
                videoMapper.updateVideoStatus(encode, status);
                //把审核表的状态修改成审核不通过

                managerMapper.updateAuditStatush(encode,0);
                //插入videoAuditTag表
                managerMapper.insertAuditStatus(video.getVideoId(), videoAuditTagId);

                return Result.success();
            }
            return Result.error();

        }catch (Exception e){
         log.error("删除视频失败");
         return Result.error();
        }
    }
}
