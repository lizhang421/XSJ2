package com.example.XSJ.service.User;

import com.example.XSJ.Mapper.UserMapper.UserMapper;
import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.User.*;
import com.example.XSJ.Pojo.UserDTO.UserBaseDate;
import com.example.XSJ.Pojo.UserDTO.UserFollowDTO;
import com.example.XSJ.Pojo.UserDTO.UserMessageDTO;
import com.example.XSJ.Pojo.Video.UserInteractDetailVO;
import com.example.XSJ.Pojo.Video.VideoPending;
import com.example.XSJ.Pojo.Video.VideoType;
import com.example.XSJ.Pojo.VideoDTo.VideoTagDTO;
import com.example.XSJ.ServiceImpl.UserServiceImpl.UserServiceImpl;
import com.example.XSJ.utils.PasswordEncoder;
import com.example.XSJ.utils.RegexUtils;
import com.example.XSJ.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserServiceImpl {
    @Autowired
    private UserMapper usermapper;
    @Override
    public Boolean IsExitAmount(String phone) {
       Integer result= usermapper.IsExitAmount(phone);

        return result != null && result > 0;
    }

    @Override
    public boolean checkexit(String userid) {
        Integer result= usermapper.IsExitId(userid);
    if ( result == 0){
        return true;
    }
        return false;
    }

    @Override
    public void insertuser(String userid, String encode, String phone, String password1,String realIp) {
        usermapper.insertuser(userid,encode,phone,password1,realIp);

    }

    @Override
    public String GetPasswordByPhone(String phone) {
       return usermapper.GetPasswordByPhone(phone);

    }

    @Override
    public UserBaseDate GetUserBaseByPhone(String phone) {

        return usermapper.GetUserBaseByPhone(phone);
    }

    @Override
    public UserMessageDTO FindUserMesageByEncode(String encode) {
        return  usermapper.GetUserMessageByEncode(encode);
    }

    /**
     * 使用映射的方式实现条件查询
     * @param enCode
     * @param filteredData
     */
    @Override
    public  int   UpdateUserMessage(String enCode, Map<String, Object> filteredData) {
        int affectedRows =  usermapper.updateUserByDynamicFields(enCode,filteredData);
        return affectedRows;
    }

    @Override
    public String getUserNameById(String publisherId) {

        return usermapper.getUserNameById(publisherId);
    }

    @Override
    public List<UserMessage> GetUserMessageDate(String encode) {
        return usermapper.GetUserDataByEncode( encode);
    }

    @Override
    public int UpdateUserlanguage(String encode, String language) {
        return usermapper.updateuserlanguage(encode,language);
    }

    @Override
    public List<UserFollowDTO> FindUserFollow(String encode) {
        return  usermapper.getfinduserfollow(encode);
    }

    @Override
    public List<VideoTagDTO> FindUserVideopost(String encode) {
        return usermapper.getfinduserpost(encode);
    }

    @Override
    public List<VideoTagDTO> FindUserVideoCollect(String encode) {
        return usermapper.getuservollect(encode);
    }

    @Override
    public List<VideoTagDTO> FindUserVideoLike(String encode) {
        return usermapper.getfinduserlike(encode);
    }

    @Override
    public int cancelFollow(String userEncode, String enCode, int status) {
        // 先查有没有记录
        int count = usermapper.checkFollowExist(userEncode, enCode);

      try {
          if (count == 0) {
              //插入关注信息
              usermapper.insertFollow(userEncode, enCode, status);

          }

          if (count > 0) {
              // 有记录 → 执行更新
              return usermapper.cancelFollow(userEncode, enCode, status);
          }

          return 1;
      }catch (Exception e){
          log.error("取消关注失败", e);
          return 0;
      }


    }


    @Override
    public void updateUserInterest(String userId, String interestJson) {
        usermapper.updateUserInterest(userId,interestJson);
    }

    @Override
    public Oothermessge GetOtherMessageDate(String encode) {

       return  usermapper.GetOtherMessageDate(encode);
    }

    @Override
    public Map<String, Object> FindUserInterest(String userEncode) {

        Map<String, Object> result = new HashMap<>();
        List<UserInteractDetailVO> myInteractDetailVO = usermapper.FindUserInterest(userEncode);
        List<UserInteractDetailVO> otherInteractDetailVO = usermapper.FindOtherUserInterest(userEncode);

        result.put("myInteractDetailVO", myInteractDetailVO);
        result.put("otherInteractDetailVO", otherInteractDetailVO);
        return result;

    }

    @Override
    public Result UpdateUserInfoMessage(String userEncode, UserUpdateDTO userUpdateDTO) {
        String avatarURL = userUpdateDTO.getAvatar();
        String backImageURL = userUpdateDTO.getBackImage();
        String userid=usermapper.getuserid(userEncode);

        if (avatarURL != null && !avatarURL.isBlank()) {
            //查询是否有待审核的头像
            int pendingCount = usermapper.getAvatarPending(userid);

            if (pendingCount > 0) {
               return Result.error("已经存在待审核头像");
            }
            usermapper.addUserAvater(userid, avatarURL, "AVATAR");
            userUpdateDTO.setAvatar(null); // 清空防止意外更新
        }

        if (backImageURL != null && !backImageURL.isBlank()) {
            int pendingCount = usermapper.getBackImagePending(userid);
            if (pendingCount > 0) {
                return Result.error("已经存在待审核背景");
            }
            usermapper.addUserAvater(userid, backImageURL, "BACKGROUND");
            userUpdateDTO.setBackImage(null); // 清空防止意外更新
        }
        usermapper.UpdateUserInfoMessage(userEncode, userUpdateDTO);
        return Result.success();
    }


    @Override
    public List<VideoType> getTypeList() {
        return usermapper.getalltype();
    }

    @Override
    public List<VideoPending> FindUserVideoAudit(String encode) {
        return  usermapper.FindUserVideoAudit(encode);
    }

    @Override
    public Result report(String targetUserEncode, int reportType, String targetEncode, String reason, String remark) {
        Map<String, Object>map= ThreadLocalUtil.get();
        String userEncode = (String) map.get("EnCode");

        String userId = usermapper.getuserid(userEncode);

        if (reportType==1){
            //举报的视频传的是encode
            if (targetUserEncode.equals(userEncode)){
                return Result.error("不能举报自己");
            }
        }
        if (reportType==2){
            //举报的作者传的是id
            if (targetUserEncode.equals(userId)){
                return Result.error("不能举报自己");
            }
        }


        String reportId = UUID.randomUUID().toString();
        int role=usermapper.insertreport(reportId,userEncode,targetUserEncode,reportType,targetEncode,reason,remark);
        if (role==1){
            return Result.success();
        }
        return Result.error("举报失败，稍后再试");
    }

    @Override
    public Result getreport() {
     try {
         Map<String, Object>map= ThreadLocalUtil.get();

        String userEncode = (String) map.get("EnCode");
        String userId = usermapper.getuserid(userEncode);
        List<ReportVO> mysendreport=usermapper.getreport(userId);
        List<ReportVO> mygetreport=usermapper.getgetreport(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("mysendreport",mysendreport);
        result.put("mygetreport",mygetreport);
        return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取失败");
        }

    }

    @Override
    public Result readreport(List<String> reportId) {

        try {
            Map<String, Object> map = ThreadLocalUtil.get();

            String userEncode = (String) map.get("EnCode");
            String userId = usermapper.getuserid(userEncode);

            int role = usermapper.readreport(reportId, userId);
            if (role == 1) {
                return Result.success();
            }
            return Result.error("");
        }catch (Exception e){
            log.error("获取失败",e);
            return Result.error("");
        }
    }

    @Override
    public Result updatePassword(String userEncode, String oldPassword, String password) {
        try {
            String oldpassworddb=usermapper.getPassword(userEncode);
            if (RegexUtils.isPasswordInvalid(password)){
                return  Result.error("密码格式不正确");
            }
            if (!PasswordEncoder.matches(oldpassworddb,oldPassword)){
                return Result.error("原密码错误");
            }
            //检查新旧密码
            if (PasswordEncoder.matches(oldpassworddb,password)){

                return Result.error("新密码不能与原密码相同");
            }

            String newpassword=PasswordEncoder.encode(password);

           int role= usermapper.updatePassword(userEncode,newpassword);
            if (role==1){
                return Result.success();
            }
            return Result.error("修改密码失败");
        }catch (Exception e){
            log.error("修改密码异常",e);
            return Result.error("系统繁忙，请稍后再试");
        }

    }

    @Override
    public boolean isFriend(String userEncode, String encode) {
        Integer count = usermapper.isFriendByEncode(userEncode, encode);
        return count != null && count > 0;
    }

    @Override
    public boolean isFollow(String encode, String userEncode) {
        Integer count = usermapper.checkIsFollowed(userEncode,encode);
        return count != null && count > 0;

    }

    @Override
    public List<UserMessageDTO> FindUserMessageByCode(String key) {

        return usermapper.FindUserMessageByCode(key);
    }


}
