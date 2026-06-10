package com.example.XSJ.ServiceImpl.UserServiceImpl;

import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.User.Oothermessge;
import com.example.XSJ.Pojo.User.UserMessage;
import com.example.XSJ.Pojo.User.UserUpdateDTO;
import com.example.XSJ.Pojo.UserDTO.UserBaseDate;
import com.example.XSJ.Pojo.UserDTO.UserFollowDTO;
import com.example.XSJ.Pojo.UserDTO.UserMessageDTO;
import com.example.XSJ.Pojo.Video.VideoPending;
import com.example.XSJ.Pojo.Video.VideoType;
import com.example.XSJ.Pojo.VideoDTo.VideoTagDTO;

import java.util.List;
import java.util.Map;

public interface UserServiceImpl {
    Boolean IsExitAmount(String phone);


    boolean checkexit(String userid);

    void insertuser(String userid, String encode, String phone, String password1,String realIp);

    String GetPasswordByPhone(String phone);

    UserBaseDate GetUserBaseByPhone(String phone);

    UserMessageDTO FindUserMesageByEncode(String encode);

    int UpdateUserMessage(String enCode, Map<String, Object> filteredData);

    String getUserNameById(String publisherId);

    List<UserMessage> GetUserMessageDate(String encode);

    int UpdateUserlanguage(String encode, String language);

    List<UserFollowDTO> FindUserFollow(String encode);

    List<VideoTagDTO> FindUserVideopost(String encode);

    List<VideoTagDTO> FindUserVideoCollect(String encode);

    List<VideoTagDTO> FindUserVideoLike(String encode);

    int cancelFollow(String userEncode, String enCode, int status);

    void updateUserInterest(String userId, String interestJson);

    Oothermessge GetOtherMessageDate(String encode);

    Map<String, Object> FindUserInterest(String userEncode);

    Result UpdateUserInfoMessage(String userEncode, UserUpdateDTO userUpdateDTO);

    List<VideoType> getTypeList();

    List<VideoPending> FindUserVideoAudit(String encode);

    Result report(String targetUserEncode, int reportType, String targetEncode, String reason, String remark);

    Result getreport();

    Result readreport(List<String> reportId);

    Result updatePassword(String userEncode, String oldPassword, String password);

    boolean isFriend(String userEncode, String encode);

    boolean isFollow(String encode, String userEncode);

    List<UserMessageDTO> FindUserMessageByCode(String key);
}
