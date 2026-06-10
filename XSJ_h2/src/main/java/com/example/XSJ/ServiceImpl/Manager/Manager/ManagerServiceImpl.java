package com.example.XSJ.ServiceImpl.Manager.Manager;

import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.System.PoolExtractRuleUpdateDTO;
import com.example.XSJ.Pojo.System.SysScoewConfig;
import com.example.XSJ.Pojo.System.SysScoreConfig;
import com.example.XSJ.Pojo.System.UserReview;
import com.example.XSJ.Pojo.User.User;
import com.example.XSJ.Pojo.User.UserUpdateDTO;
import com.example.XSJ.Pojo.Video.Video;
import com.example.XSJ.Pojo.Video.VideoAuditTag;
import com.example.XSJ.Pojo.Video.VideoPoolConfig;
import com.example.XSJ.Pojo.VideoDTo.VideoPendingDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ManagerServiceImpl {
    User findmanagermessagebyphone(String phone);

    List<VideoPendingDTO> getauditvideo();

    void auditVideo(String videoPendingId, int reviewStatus, List<String> videoAuditTagId);

    List<UserReview> getShenHeImage(List<Integer> status);

    List<VideoAuditTag> getauditVideoResult();

    void uploadShenHeImageResult(String userviewid, String videoAuditTagId, int result);

    SysScoreConfig getSysScoreConfig();

    List<Video> getVideoList(int currentPage, int offset);

    void updateVideoSortWeight(Video video);

    VideoPoolConfig getVideoPoolConfig(String videoId);

    String getRealPoolIdByCategoryAndType(String useCategory, String targetPoolType);

    void leavePool(String videoId, String oldPoolId, String s);

    boolean isInPool(String videoId, String targetPoolId);

    void joinPool(String videoId, String targetPoolId, String s);

    void updateVideoCurrentPool(Video video);

    String getCurrentVideoPoolId(String videoId);

    void leaveAllPools(String videoId, String s);

    Result getUserList(int pageNum, int pageSize, Integer status, String key, Integer roleBase);

    Result updateUserStatus(String encode, Integer status);

    Result getManagerMessage();

    Result ManagerSysConfig();

    Result updatesysSortWeight(SysScoewConfig sysScoewConfig);

    Result updatePoolConfig(PoolExtractRuleUpdateDTO poolExtractRuleUpdateDTO);

    Result addPool(PoolExtractRuleUpdateDTO poolExtractRuleUpdateDTO);

    Result deletePool(Long poolId);

    Result getjubao(int pageNum, int pageSize, Integer status, Integer type, LocalDateTime startTime, LocalDateTime endTime);

    Result solvejubao(String jubaoId, Integer jieguo, Integer solvechufa);

    Result updateUserMessage(UserUpdateDTO userUpdateDTO);

    Result getSysResourceConfig();

    Result updateSysResourceConfig(String homeresourse, String url);

    Result getdataease();

    Result instermanager(String phone, String password, String nickname, String realname);
}
