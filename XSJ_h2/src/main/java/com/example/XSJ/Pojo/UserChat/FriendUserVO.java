package com.example.XSJ.Pojo.UserChat;

import lombok.Data;

@Data
public class FriendUserVO {

    private Integer status;

    private String enCode;    // 好友Encode
    private String nickName;  // 好友昵称
    private String avatar;    // 好友头像
    private String remark;    // 好友备注
}