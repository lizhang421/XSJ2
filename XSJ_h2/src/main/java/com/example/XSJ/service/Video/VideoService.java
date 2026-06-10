package com.example.XSJ.service.Video;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.nacos.utils.StringUtils;
import com.example.XSJ.Mapper.VideoMapper.VideoMapper;
import com.example.XSJ.Pojo.PoolExtractRule;
import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.System.UserReview;
import com.example.XSJ.Pojo.User.AccountStatus;
import com.example.XSJ.Pojo.UserChat.ReadMessageDTO;
import com.example.XSJ.Pojo.Video.*;

import com.example.XSJ.Pojo.Video.VideoResolutionDTO;
import com.example.XSJ.Pojo.VideoDTo.*;
import com.example.XSJ.ServiceImpl.VideoServiceImpl.VideoServiceImpl;
import com.example.XSJ.utils.*;
import com.example.XSJ.utils.Video.VideoCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Slf4j
@Service
public class VideoService implements VideoServiceImpl {

    @Autowired
    private VideoMapper  videoMapper;


    @Autowired
    private AliVideoUtil AliVideoUtil;


    @Autowired
    private AliOssUtil aliVideoUtil;

    private VideoCache videoCache=new VideoCache();

    @Override
    public boolean check(Map<String, String> map) {
        //如果能查询出来就返回false
        String VideoId=map.get("VideoId");
        String VideoEnCode=map.get("VideoEnCode");

        if (VideoId==null||VideoEnCode==null||VideoEnCode.isEmpty()||VideoId.isEmpty()){
            return false;
        }
        int count =videoMapper.getdate(VideoId,VideoEnCode);
       if(count>0){
           return false;
       }
       return true;
    }

    @Override
    public String getuserdatebycode(String code) {
        return videoMapper.getdatebycode(code);
    }

    @Override
    public Map<String, Map<String, Object>> findPoolbyType(List<VideoPoolConfig> videoPoolConfigList) {

        Map<String, Map<String, Object>> finalMap = new HashMap<>();

        if (videoPoolConfigList == null || videoPoolConfigList.isEmpty()) {
            log.info("传入的视频池配置列表为空，无需处理视频查询");
            return finalMap;
        }

        // 遍历前面已经获取到的池配置列表
        for (VideoPoolConfig poolConfig : videoPoolConfigList) {
            String poolId = poolConfig.getVideoPoolConfigId();
            if (poolId == null || poolId.trim().isEmpty()) {
                log.warn("当前池配置无有效 poolId，跳过该池处理");
                continue;
            }

            List<Video> videos = videoMapper.getvideolistbypool(poolId);
            if (videos == null || videos.isEmpty()) {
                log.warn("池ID：{} 未查询到对应视频列表，跳过该池", poolId);
                continue;
            }

            Map<String, Object> innerMap = new HashMap<>();
            innerMap.put("PoolMount", poolConfig.getGetPoolMount());
            innerMap.put("Type", poolConfig.getPoolType());
            innerMap.put("Category", poolConfig.getPoolCaetGory());
            innerMap.put("PoolId", poolId);
            innerMap.put("Videos", videos);


            finalMap.put(poolId, innerMap);

            videoCache.poolVideoCache.put(poolId, videos);

            log.info("池ID：{} 处理完成，封装视频数量：{}，已存入结果集和缓存", poolId, videos.size());
        }

        return finalMap;
    }

    /**
     * 查询poolid 查询整体信息
     * @param poolId
     * @return
     */
    @Override
    public VideoPoolConfig queryPoolInfoFromDb(String poolId) {
        return videoMapper.getdatebypoolId(poolId);
    }

    @Override
    public List<String> queryPoolIdsFromDb(String standardCategory) {
        /**
         * 根据汉字的属性去筛选id列表
         */
        List<String> poolIdList = new ArrayList<>();
        poolIdList=videoMapper.findpoolidbytype(standardCategory);
        return poolIdList;
    }

    @Override
    public List<Video> findPoolVideobyPoolId(String poolId) {
        List<Video> finalMap =new ArrayList<>();
        if (poolId == null || poolId.trim().isEmpty()) {
            log.warn("没有获得到当前要查询池的id");
        }
        finalMap=videoMapper.findpoolvideobypoolid(poolId);
       return finalMap;
    }

    @Override
    public List<Video> findVideoRandom(int mount, List<Video> noInVideo) {

        List<Video> safeNoInVideo = Objects.isNull(noInVideo) ? new ArrayList<>() : noInVideo;

        // 提取需要排除的视频ID（此时safeNoInVideo一定非null）
        List<String> excludeVideoIds = safeNoInVideo.stream()
                .map(Video::getVideoId)
                .filter(Objects::nonNull) // 过滤掉null的videoId，避免查库出错
                .collect(Collectors.toList());

        // 调用Mapper查询随机视频（即使excludeVideoIds为空，也能正常查询）
        List<Video> randomVideos = videoMapper.getRandomVideo(mount, excludeVideoIds);

        // 如果Mapper返回null，返回空Lis避免上层代码再报NPE
        return Objects.isNull(randomVideos) ? new ArrayList<>() : randomVideos;
    }

    @Override
    public PoolExtractRule findPoolConfig(String poolType) {

        return videoMapper.getPoolVideoConfig(poolType);
    }

    @Override
    public List<VideoFileDTO> findVideoResourcesByCode(String enCode) {
        return videoMapper.findVideoResourcesByCode(enCode);
    }

    @Override
    public List<CommentDTO> findVideoCommentsByCode(String encode) {

        return  videoMapper.findVideoCommentsByCode(encode);

    }

    @Override
    public List<VideoFileDTO> findVideoFileByCode(String encode) {
        return  videoMapper.findVideoFileByCode(encode);
    }

    @Override
    public List<UserResourcesDTO> findUserResourcesByCode(String encode) {
        return  videoMapper.findUserResourcesByCode(encode);
    }

    @Override
    public List<VideoRecommendDTO> findVideoTagByCode(String encode) {
        return videoMapper.findRecommendVideosWithCollection(encode);
    }

    @Override
    public void InsertIntoComment(CommentSubmitDTO commentDTO, String userEncode) {

        String videoEnCode = commentDTO.getEnCode();

        String parentId = commentDTO.getParentId();
        if (parentId == null || parentId.trim().isEmpty() || "0".equals(parentId.trim())) {
            parentId = null;
        }
        String videoCommentId = generateCommentId();

        int affectedRows = videoMapper.InsertIntoComment(
                videoCommentId, videoEnCode, userEncode, commentDTO.getComment(), parentId, LocalDateTime.now()
        );

        if (affectedRows == 0) {

            throw new RuntimeException("评论插入失败：请检查视频编码[" + videoEnCode + "]或用户编码[" + userEncode + "]是否存在且状态为1");
        }
    }

    @Override
    public UserVideoInteract checkVideoInteract(String videoEnCode, String userEncode) {
        return videoMapper.checkVideoInteract(videoEnCode, userEncode);
    }



    private void updateVideoStatNum(String videoEnCode, String operateType, Integer status) {
        // 只处理这四种操作
        if (!"like".equals(operateType) &&
                !"comment".equals(operateType) &&
                !"share".equals(operateType) &&
                !"collect".equals(operateType)) {
            return;
        }
        int num = status == 1 ? 1 : -1;
        videoMapper.updateVideoStatNum(videoEnCode, operateType, num);
    }

    // 原有 createVideoInteract 方法（末尾新增调用）
    @Override
    public void createVideoInteract(String videoEnCode, String userEncode, VideoInteractReqDTO videoInteractReqDTO) {
        // 1. 生成VI开头32位唯一ID
        String interactId = IdGeneratorUtil.generateVI32Id();

        // 2. 构建互动记录实体
        UserVideoInteract interact = new UserVideoInteract();
        interact.setId(interactId);
        interact.setCreateTime(LocalDateTime.now());
        interact.setUpdateTime(LocalDateTime.now());
        // 3. 初始化操作状态默认值
        interact.setIsLike(0);
        interact.setIsCollect(0);
        interact.setIsComment(0);
        interact.setIsShare(0);
        interact.setPlayPercent(0);
        interact.setLastPosition(0);
        interact.setInteractScore(0);

        // 4. 同步用户当前操作
        String operateType = null;
        Integer status = null;
        if (videoInteractReqDTO != null) {
            operateType = videoInteractReqDTO.getOperateType();
            status = videoInteractReqDTO.getStatus();

            if (StringUtils.isNotBlank(operateType) && status != null && (status == 0 || status == 1)) {
                switch (operateType) {
                    case "like":
                        interact.setIsLike(status);
                        break;
                    case "collect":
                        interact.setIsCollect(status);
                        break;
                    case "comment":
                        interact.setIsComment(status);
                        break;
                    case "share":
                        interact.setIsShare(status);
                        break;
                    default:
                        break;
                }
            }
        }

        // 5. 执行插入
        videoMapper.insertVideoInteract(interact, videoEnCode, userEncode);

        // ========== 新增：更新视频统计数 ==========
        if (StringUtils.isNotBlank(operateType) && status != null) {
            updateVideoStatNum(videoEnCode, operateType, status);
        }
    }


    @Override
    public void UpdateVideoInteract(String videoEnCode, String userEncode, VideoInteractReqDTO videoInteractReqDTO) {

        UserVideoInteract existInteract = videoMapper.checkVideoInteract(videoEnCode, userEncode);
        if (existInteract == null) {
            return;
        }


        String operateType = null;
        Integer status = null;
        if (videoInteractReqDTO != null) {
            operateType = videoInteractReqDTO.getOperateType();
            status = videoInteractReqDTO.getStatus();

            if (StringUtils.isNotBlank(operateType) && status != null && (status == 0 || status == 1)) {
                switch (operateType) {
                    case "like":
                        existInteract.setIsLike(status);
                        break;
                    case "collect":
                        existInteract.setIsCollect(status);
                        break;
                    case "comment":
                        existInteract.setIsComment(status);
                        break;
                    case "share":
                        existInteract.setIsShare(status);
                        break;
                    default:
                        return;
                }
            } else {
                return;
            }
        }

        // 3. 更新时间戳
        existInteract.setUpdateTime(LocalDateTime.now());

        // 4. 执行更新
        videoMapper.updateVideoInteract(existInteract, videoEnCode, userEncode);

        // ========== 新增：更新视频统计数 ==========
        if (StringUtils.isNotBlank(operateType) && status != null) {
            updateVideoStatNum(videoEnCode, operateType, status);
        }
    }

    @Override
    public List<VideoDateDTO> FindVideoOpdata(String enCode) {

        if (StringUtils.isBlank(enCode)){
            return List.of();
        }
        List<VideoDateDTO> videoDateDTOList= videoMapper.selectVideoOpDataByEnCode(enCode);
       return  videoDateDTOList== null ? emptyList(): videoDateDTOList;

    }

    @Override
    public int insertUserVideohietory(String historyId, String userEncode, String videoEnCode, String time, Integer watchLongTime) {

        return videoMapper.insertUserVideohietory(historyId,userEncode,videoEnCode,time,watchLongTime);
    }

    @Override
    public List<UserVideoHistoryDTO> GetUserViewHistory(String userEncode) {
        return videoMapper.getuserViewHistory(userEncode);
    }



    @Override
    public List<VideoTagDTO> FindVideoTagByCode(String key) {
        return  videoMapper.selectVideoTagByCode(key);
    }

    @Override
    public List<VideoCommentVO> GetOtherMessageDate(String userEncode) {
      return   videoMapper.GetOtherMessageDate(userEncode);

    }

    @Override
    public void UpdateUserCommentMessage( List<String> commentId) {

        videoMapper.UpdateUserCommentMessage(commentId);
    }

    @Override
    public List<VideoCommentVO> getmysent(String userEncode) {
       return videoMapper.getmysent(userEncode);
    }

    public void UpdateReadUserCommentMessage(List<String> interactId) {
        videoMapper.UpdateReadUserCommentMessage(interactId);
    }

    @Override
    public Map<String, Object> getUserAuditInfo(String userEncode) {

        Map<String, Object> result = new HashMap<>();
        List<AccountStatus> accountStatusList = videoMapper.getUserAuditInfo(userEncode);
        List<UserReview>UserReview = videoMapper.getUserReview(userEncode);
        result.put("accountStatusList", accountStatusList);
        result.put("UserReview", UserReview);
        return result;
    }

    @Override
    public void readMessage(ReadMessageDTO dto) {
        if (CollUtil.isNotEmpty(dto.getAccountStatusIds())) {
            videoMapper.batchReadAccountStatus(dto.getAccountStatusIds());
        }
        // 批量更新审核消息
        if (CollUtil.isNotEmpty(dto.getUserReviewIds())) {
            videoMapper.batchReadUserReview(dto.getUserReviewIds());
        }
    }

    /**
     * 上传视频
     * @param videoUploadDTO
     * @param userEncode
     */
    @Override
    public void uploadvideo(VideoUploadDTO videoUploadDTO, String userEncode) {
        String userId = videoMapper.getuseridbyencode(userEncode);
        VideoPending pending = new VideoPending();
        pending.setVideoPendingId(UUID.randomUUID().toString());
        pending.setEnCode("VIDEO_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        pending.setVideoName(videoUploadDTO.getVideoName());
        pending.setVideoDesc(videoUploadDTO.getVideoDesc());
        pending.setVideoUrl(videoUploadDTO.getVideoUrl());
        pending.setVideoImageUrl(videoUploadDTO.getVideoImageUrl());
        pending.setVideoTypeId(videoUploadDTO.getVideoTypeId());
        pending.setPublishCode(userEncode);
        pending.setPublisherId(userId);
        pending.setIsCollection(videoUploadDTO.getIsCollection() == null ? 0 : videoUploadDTO.getIsCollection());
        if (videoUploadDTO.getCollectionId() != null && !videoUploadDTO.getCollectionId().isBlank()) {
            pending.setCollectionId(Integer.valueOf(videoUploadDTO.getCollectionId()));
        } else {
            pending.setCollectionId(null);
        }
        pending.setVideoTime(0);
        pending.setVideoSize(0);
        pending.setVideoFormat("mp4");

        pending.setReviewStatus(0);
        pending.setVideoStatus(2);
        pending.setIsDelete(0);

        pending.setPublishTime(LocalDateTime.now());
        pending.setCreateTime(LocalDateTime.now());
        pending.setUpdateTime(LocalDateTime.now());

        // 审核信息
        pending.setAuditTime(null);
        pending.setEndAuditTime(null);
        pending.setReviewerCode(null);
        pending.setReviewerId(null);

        videoMapper.insert(pending);

        try {
            String coverUrl = videoUploadDTO.getVideoImageUrl();
            if (coverUrl == null || coverUrl.trim().isEmpty()) {

                File tempFile = AliVideoUtil.downloadVideo(videoUploadDTO.getVideoUrl());
                String videoPath = tempFile.getAbsolutePath();

                String coverPath = videoPath.replace(".mp4", "_cover.jpg");

                FFmpegUtils.extractFirstFrame(videoPath, coverPath);

                try (FileInputStream fis = new FileInputStream(coverPath)) {
                    MultipartFile mf = new MockMultipartFile(
                            "file",
                            new File(coverPath).getName(),
                            "image/jpeg",
                            fis
                    );
                    String newCoverUrl = aliVideoUtil.upload(mf);


                    videoMapper.updateVideoImageUrl(pending.getVideoPendingId(), newCoverUrl);
                }
                new File(coverPath).delete();
                tempFile.delete();
            }
        } catch (Exception ignored) {
            // 即使失败也不影响上传流程！
        }
    }

    @Override
    public Result getvideoResoultion(String enCode) {
        if (enCode==null||enCode.isBlank()){
            return Result.error();
        }
        try {
          List<VideoResolutionDTO> list= videoMapper.getvideoresolution(enCode);
          return Result.success(list);
        }catch (Exception e){
            log.error("获取视频分辨率异常",e);
            return Result.error("系统繁忙，请稍后再试");
        }

    }

    @Override
    public Result uploadVide(String fileurl, String videoEncode, String name, String type, String format, int size, String recoursedese) {

        try{
            String videoid =videoMapper.getvideoidbyencode(videoEncode);
            String resourseid="RE_"+UUID.randomUUID().toString();
            Map<String, Object> map = ThreadLocalUtil.get();
            String  userEncode = (String) map.get("EnCode");
            String userid=videoMapper.getuseridbyencode(userEncode);
            videoMapper.insertUserResources(resourseid,fileurl,name,type,format,size,recoursedese,videoid,userid);

            return Result.success();

        }catch (Exception e){
            log.error("上传视频异常",e);
            return Result.error("上传失败");
        }
    }

    @Override
    public Result deleteVideo(String enCode) {
        try {
            videoMapper.deleteVideo(enCode);
            videoMapper.deletepending(enCode);
            return  Result.success();
        }catch (Exception e){
            log.error("删除视频异常",e);
            return Result.error("删除失败");
        }
    }

    @Override
    public Result deleteVideoResource(String id) {

        try {
            videoMapper.deleteVideoResource(id);
            return   Result.success();
        }catch (Exception e){
            log.error("删除视频资源异常",e);
            return Result.error("删除失败");
        }
    }


    private String generateCommentId() {
        // 定义ID字符集（数字+大小写字母，避免特殊字符）
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder("UM"); // 固定前缀UM

        // 生成32位随机字符
        for (int i = 0; i < 32; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }




}











