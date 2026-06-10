package com.example.XSJ.ServiceImpl.VideoServiceImpl;

import com.example.XSJ.Pojo.PoolExtractRule;
import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.UserChat.ReadMessageDTO;
import com.example.XSJ.Pojo.Video.*;
import com.example.XSJ.Pojo.VideoDTo.*;


import java.util.List;
import java.util.Map;

public interface VideoServiceImpl {
    boolean check(Map<String, String> map);

    String getuserdatebycode(String code);

    Map<String, Map<String, Object>> findPoolbyType(List<VideoPoolConfig> typeList);

    VideoPoolConfig queryPoolInfoFromDb(String poolId);

    List<String> queryPoolIdsFromDb(String standardCategory);

    List<Video> findPoolVideobyPoolId(String poolId);

    List<Video> findVideoRandom(int mount, List<Video> noInVideo);


    PoolExtractRule findPoolConfig(String poolType);

    List<VideoFileDTO> findVideoResourcesByCode(String enCode);

    List<CommentDTO> findVideoCommentsByCode(String encode);

    List<VideoFileDTO> findVideoFileByCode(String encode);

    List<UserResourcesDTO> findUserResourcesByCode(String encode);

    List<VideoRecommendDTO> findVideoTagByCode(String encode);

    void InsertIntoComment(CommentSubmitDTO commentDTO, String userEncode);


    UserVideoInteract checkVideoInteract(String videoEnCode, String userEncode);

    void createVideoInteract(String videoEnCode, String userEncode, VideoInteractReqDTO videoInteractReqDTO);

    void UpdateVideoInteract(String videoEnCode, String userEncode, VideoInteractReqDTO videoInteractReqDTO);

    List<VideoDateDTO> FindVideoOpdata(String enCode);


    int insertUserVideohietory(String historyId, String userEncode, String videoEnCode, String time, Integer watchLongTime);

    List<UserVideoHistoryDTO> GetUserViewHistory(String userEncode);



    List<VideoTagDTO> FindVideoTagByCode(String key);

    List<VideoCommentVO> GetOtherMessageDate(String userEncode);

    void UpdateUserCommentMessage( List<String> commentId);

    List<VideoCommentVO> getmysent(String userEncode);

    void UpdateReadUserCommentMessage(List<String> interactId);

    Map<String, Object> getUserAuditInfo(String userEncode);

    void readMessage(ReadMessageDTO dto);

    void uploadvideo(VideoUploadDTO videoUploadDTO, String userEncode);

    Result getvideoResoultion(String enCode);

    Result uploadVide(String fileurl, String videoEncode, String name, String type, String format, int size, String recoursedese);

    Result deleteVideo(String enCode);

    Result deleteVideoResource(String id);
}
