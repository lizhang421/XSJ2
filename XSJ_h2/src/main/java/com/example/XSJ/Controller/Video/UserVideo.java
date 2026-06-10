package com.example.XSJ.Controller.Video;


import com.alibaba.cloud.nacos.utils.StringUtils;
import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.UserDTO.UserMessageDTO;
import com.example.XSJ.Pojo.Video.UserVideoInteract;
import com.example.XSJ.Pojo.Video.Video;
import com.example.XSJ.Pojo.Video.VideoCommentVO;
import com.example.XSJ.Pojo.Video.VideoVO;
import com.example.XSJ.Pojo.VideoDTo.*;
import com.example.XSJ.ServiceImpl.VideoServiceImpl.VideoServiceImpl;
import com.example.XSJ.service.User.UserService;
import com.example.XSJ.service.Video.VideoService;
import com.example.XSJ.utils.ThreadLocalUtil;
import com.example.XSJ.utils.Video.VideoGet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

/**
 *
 * 用户上传视频
 */

@Slf4j
@RestController
@Transactional
@RequestMapping("/Video")
public class UserVideo {
    @Autowired
    private VideoGet videoGet;


    @Autowired
    private UserService userService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private RestTemplate restTemplate;


    //获得用户推荐视频的接口
    @GetMapping("/Videos")
    public Result UserVideos(
            @RequestParam(value = "mount", required = true) int mount
    ) {
        Map<String, Object> date = ThreadLocalUtil.get();
        String encode = date.get("EnCode").toString();
        List<Video> finalVideoList = videoGet.VideoRecommendations(encode, mount);
        if (finalVideoList.size() == 0) {
            return Result.error("非常抱歉，系统异常");
        }
        List<VideoVO> videoVOList = new ArrayList<>();
        for (Video video : finalVideoList) {
            VideoVO vo = new VideoVO();
            vo.setEnCode(video.getEnCode());
            vo.setVideoName(video.getVideoName());
            vo.setVideoImageUrl(video.getVideoImageUrl());
            vo.setVideoUrl(video.getVideoUrl());
            vo.setVideoTime(video.getVideoTime());
            vo.setViewCount(video.getViewCount());
            vo.setLikeCount(video.getLikeCount());
            vo.setCollectCount(video.getCollectCount());
            vo.setVideoResolution(video.getVideoResolution());
            vo.setCommentCount(video.getCommentCount());
            vo.setShareCount(video.getShareCount());
            vo.setIsFeature(video.getIsFeature());
            vo.setIsMember(video.getIsMember() == 1);
            vo.setIsCollection(video.getIsCollection() == 1);
            vo.setIntroduction(video.getIntroduction());

            String publisherId = video.getPublisherId();
            if (publisherId != null && !publisherId.isEmpty()) {
                String publisherName = userService.getUserNameById(publisherId);
                // 兜底：如果查询不到名称，显示默认值
                vo.setPublisherName(publisherName == null ? "未知发布人" : publisherName);
            } else {
                vo.setPublisherName("未知发布人");
            }
            videoVOList.add(vo);
        }
        return Result.success(videoVOList);
    }

    /**
     * 根据视频编码查询配套资源
     *
     * @param enCode
     * @return 资源列表
     */
    @GetMapping("/video-file")
    public Result getVideoFile(
            @RequestParam(value = "enCode", required = true) String enCode
    ) {
        if (StringUtils.isBlank(enCode)) {
            log.warn("获取视频文件失败：入参enCode为空");
            return Result.error("参数错误：视频编码不能为空");
        }
        try {
            List<VideoFileDTO> videoResources = videoService.findVideoResourcesByCode(enCode);

            if (videoResources == null) {
                videoResources = Collections.emptyList();
            }

            log.info("获取视频文件成功，enCode：{}，资源数量：{}", enCode, videoResources.size());

            return Result.success(videoResources);

        } catch (IllegalArgumentException e) {

            log.error("获取视频文件失败：入参enCode非法，enCode：{}", enCode, e);
            return Result.error("参数错误：视频编码格式非法");
        } catch (Exception e) {
            log.error("获取视频文件异常，enCode：{}", enCode, e);
            return Result.error("系统繁忙，请稍后再试");
        }
    }


    @PostMapping("/video-comment")
    public Result InsertVideoComment(
            @Validated @RequestBody CommentSubmitDTO commentDTO
    ) {
        Map<String, Object> date = ThreadLocalUtil.get();
        String UserEncode = date.get("EnCode").toString();
        if (UserEncode.isEmpty() || UserEncode == null) {
            return Result.error("请先登录");
        }
        try {
            videoService.InsertIntoComment(commentDTO, UserEncode);

        } catch (Exception e) {
            log.error("添加评论异常", e);
            return Result.error("稍后再试");
        }

        return Result.success("发布成功");

    }


    @PostMapping("/video-operate")
    public Result insertVideooperate(
            @Validated @RequestBody VideoInteractReqDTO videoInteractReqDTO
    ) {
        //从当前登录的我线程中获取用户的id安全方便
        Map<String, Object> date = ThreadLocalUtil.get();
        String UserEncode = date.get("EnCode").toString();
        if (UserEncode.isEmpty() || UserEncode == null) {
            return Result.error("请先登录");
        }
        String videoEnCode = videoInteractReqDTO.getEnCode();
        try {
            //检查用户是否已经操作过这个视频
            UserVideoInteract userVideoInteract = videoService.checkVideoInteract(videoEnCode, UserEncode);

            if (userVideoInteract == null) {
                //不存在就创建一个
                videoService.createVideoInteract(videoEnCode, UserEncode, videoInteractReqDTO);
            } else {
                //存在就更新
                videoService.UpdateVideoInteract(videoEnCode, UserEncode, videoInteractReqDTO);

            }
        } catch (Exception e) {
            log.error("添加评论异常", e);
            return Result.error("稍后再试");
        }
        return Result.success("操作成功");
    }


    /**
     * 获取视频操作信息
     *  这里要获得用户现在有没有对这个视频做出了操作有就返回，没有就返回空
     *  2获得这个视频的点赞收藏关注的数量
     * @param enCode
     * @return
     */

    @GetMapping("/video-opmessage")
    public Result getVideoOperateMessage(
            @RequestParam(value = "enCode", required = true) String enCode
    ) {
        Map<String, Object>  map= ThreadLocalUtil.get();
        String UserEncode = map.get("EnCode").toString();
        if (UserEncode.isEmpty() || UserEncode == null) {
            return Result.error("请先登录");
        }
        try {

            List<VideoDateDTO> videoDateDTOList= videoService.FindVideoOpdata(enCode);
            if (videoDateDTOList==null){
                return Result.error("系统繁忙");
            }
            return Result.success(videoDateDTOList);

        }catch (Exception e) {
            log.error("获取视频操作信息异常", e);
            return Result.error("系统繁忙");
        }

    }
    //历史记录，用户视频播放百分比小于百分之5或者时长小于十秒的视频不记录
    @PostMapping("/video-history")
    public Result insertVideoHistory(
            @Validated @RequestBody UserViewHistoryDTO userViewHistoryDTO

    ) {
        //从当前线程中获取用户的代码
        Map<String, Object>  map= ThreadLocalUtil.get();
        String UserEncode = map.get("EnCode").toString();

        if (UserEncode.isEmpty() || UserEncode == null) {
            return Result.error("请先登录");
        }
        try {
            Integer watchLongTime=userViewHistoryDTO.getDuration();
            String  videoEnCode=userViewHistoryDTO.getEnCode();
            String  time =userViewHistoryDTO.getWatchTime();
            //生成32位UUID
            String uuid = UUID.randomUUID().toString();
            String historyId="HI"+uuid;

            //向数据库添加本次插入数据
            int result= videoService.insertUserVideohietory(historyId,UserEncode,videoEnCode, time,watchLongTime);
            if (result==1){
                return Result.success("添加成功");
            }else {
                return Result.error("添加失败");
            }
        }catch (Exception e){
            log.error("添加历史记录异常", e);
            return Result.error("稍后再试");
        }

    }

   /**
     * 获取用户历史记录

     * @return
     */
   @GetMapping("/look-history")
    public Result getUserHistory(
    ) {
       Map<String, Object>  map= ThreadLocalUtil.get();
           String UserEncode = map.get("EnCode").toString();
       if (UserEncode.isEmpty() || UserEncode == null) {
           return Result.error("请先登录");
       }
       try {
           List<UserVideoHistoryDTO> userViewHistoryDTOList= videoService.GetUserViewHistory(UserEncode);
           if (userViewHistoryDTOList==null){
               return Result.error("系统繁忙");
           }
           return Result.success(userViewHistoryDTOList);

       }catch (Exception e) {
           log.error("获取用户历史记录异常", e);
           return Result.error("系统繁忙");
       }

    }

    /**
     *  搜索接口
     * @return
     */

    @GetMapping("/search")
    public Result search(
            @RequestParam(value = "key", required = true) String key
    ) {
        // 正确判空
        if (key == null || key.isEmpty()) {
            return Result.error("请输入关键字");
        }

        try {
            List<VideoTagDTO> videoTagDTOS = videoService.FindVideoTagByCode(key);


            List<UserMessageDTO> userMessageDTOS = userService.FindUserMessageByCode(key);

            Map<String, Object> map = new HashMap<>();
            map.put("video", videoTagDTOS);
            map.put("user", userMessageDTOS);

            return Result.success(map);

        } catch (Exception e) {
            log.error("搜索接口异常", e);
            return Result.error("系统繁忙");
        }
    }


    /**
     * 获用户获取未读的消息记录，用户消息界面要显示回复的消息，未读的要给出提示是哪个视频
     * @return
     */
    @GetMapping("/get-user-commentmessage")
    public Result getUserCommentMessage(
    ) {
        Map<String, Object>  map= ThreadLocalUtil.get();
        String UserEncode = map.get("EnCode").toString();
        if (UserEncode.isEmpty() || UserEncode == null) {
            return Result.error("请先登录");
        }
        try {
            List<VideoCommentVO> list = videoService.GetOtherMessageDate(UserEncode);
            List<VideoCommentVO>sent=videoService.getmysent(UserEncode);
            Map<String, Object> data = new HashMap<>();
            data.put("replyMe", list);
            data.put("sent", sent);

            return Result.success(data);
        }catch (Exception e){
            log.error("获取用户消息异常", e);
            return Result.error("系统繁忙");
        }

    }

    /**
     * 用户已读谁回复了我
     * @return
     */
    @PostMapping("/user-commentmessage")
    public Result userComentMessage(@RequestBody List<String> CommentId ){

        try {
            videoService.UpdateUserCommentMessage(CommentId);
            return Result.success("");
        }catch (Exception e){
            log.error("用户已读消息异常", e);
            return Result.error("系统繁忙");
        }
    }

    @GetMapping("/get-user-intercate")
    public Result getUserIntercate(
    ) {
        Map<String, Object>  map= ThreadLocalUtil.get();
        String UserEncode = map.get("EnCode").toString();
        if (UserEncode.isEmpty() || UserEncode == null) {
            return Result.error("请先登录");
        }
        try {
            Map<String,Object> mapDate = userService.FindUserInterest(UserEncode);
            return Result.success(mapDate);
        }catch (Exception e){
            log.error("获取用户消息异常", e);
            return Result.error("系统繁忙");
        }
    }

    //修改互动已读状态
    @PostMapping("/update-interact-status")
    public Result updateInteractStatus(
            @RequestBody List<String> interactId
    ){
        try {
            videoService.UpdateReadUserCommentMessage(interactId);
            return Result.success("");
        }catch (Exception e){
            log.error("用户已读消息异常", e);
            return Result.error("系统繁忙");
        }
    }




     @PostMapping("/upload-video")
    public Result uploadvideo(
            @Validated @RequestBody VideoUploadDTO videoUploadDTO
     ){
         Map<String, Object>  map= ThreadLocalUtil.get();
         String UserEncode = map.get("EnCode").toString();
         if (UserEncode.isEmpty() || UserEncode == null) {
             return Result.error("请先登录");
         }
         try {
             videoService.uploadvideo(videoUploadDTO,UserEncode);
             return  Result.success("上传成功");
         }catch (Exception e){
             log.error("用户上传视频异常", e);
             return Result.error("系统繁忙");
         }
     }


    @PostMapping("/video/resource")
    public Result uploadvideoResource(String  fileurl , String videoEncode, String name, String  type ,String format,int size,String recoursedese) {
        try {
            Result result = videoService.uploadVide(fileurl,videoEncode,name,type,format, size,recoursedese);
            return result;

        } catch (Exception e) {
            return Result.error("上传失败");
        }

    }


    /**
     * 删除视频
     * @param enCode
     * @return
     */
    @PostMapping("/delete-video")
    public Result deleteVideo(
            @RequestParam(value = "enCode", required = true) String enCode
    ) {
      try {
          Result result = videoService.deleteVideo(enCode);
          return result;

      }catch (Exception e){
          log.error("删除视频异常", e);
          return Result.error("系统繁忙");
      }
    }

    /**
     * 删除上传的文件

     * @return
     */
    @PostMapping("/delete-video-resource")
    public Result deleteVideoResource(
            @RequestParam(value = "enCode", required = true) String id
    ) {
        try {
            Result result = videoService.deleteVideoResource(id);
            return result;

        }catch (Exception e){
            log.error("删除视频异常", e);
            return Result.error("系统繁忙");
        }
    }




/**
 * 获取视频的其他分辨率
 */
    @GetMapping("/videoresoultion")
    public Result getVideoResoultion(
            @RequestParam(value = "enCode", required = true) String enCode
    ) {
            try {
                Result result=videoService.getvideoResoultion(enCode);
                return result;

            }catch (Exception e){
                log.error("获取视频其他分辨率异常", e);
                return Result.error("系统繁忙");
            }

    }



















}











