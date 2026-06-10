package com.example.XSJ.ServiceImpl.USerChat;

import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.UserChat.ApplyFriendDTO;
import com.example.XSJ.Pojo.UserChat.ChatMessageVO;
import com.example.XSJ.Pojo.UserChat.FriendUserVO;

import java.util.List;

public interface UserChatServiceImpl {
    void addFriend(String userEncode, String toUserEncode);

    void agree(String userEncode, String toUserEncode, int status);

    List<FriendUserVO> frindlist(String userEncode);

    ApplyFriendDTO applylist(String userEncode);

    List<ChatMessageVO> chatHistory(String toUserEncode);

    void read(List<String> messageIds, String toUserEncode);


    void deleteMessages(String toUserEncode, List<String> msgIdList);

    Result deleteSession(String toUserEncode);
}
