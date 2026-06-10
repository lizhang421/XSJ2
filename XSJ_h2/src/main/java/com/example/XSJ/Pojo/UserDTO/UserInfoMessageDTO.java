package com.example.XSJ.Pojo.UserDTO;

import com.example.XSJ.Pojo.Video.VideoPending;
import com.example.XSJ.Pojo.VideoDTo.VideoRecommendDTO;
import com.example.XSJ.Pojo.VideoDTo.VideoTagDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserInfoMessageDTO {
   //用户关注的信息
  private  List<UserFollowDTO> userFollowDTOList;
  //用户发表的视频
  private  List<VideoTagDTO>  userVideoPost;
  //用户收藏的视频
    private  List<VideoTagDTO>  userVideoCollect;
    //用户喜欢的视频
    private  List<VideoTagDTO>  userVideoLike;

    private   List<VideoPending> userVideoPending;

}
