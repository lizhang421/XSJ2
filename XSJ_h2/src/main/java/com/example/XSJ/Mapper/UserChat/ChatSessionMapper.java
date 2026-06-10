package com.example.XSJ.Mapper.UserChat;

import com.example.XSJ.Pojo.UserChat.ChatSession;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface ChatSessionMapper {

    // 查询会话
    @Select("SELECT * FROM chatsession WHERE sessionCode = #{sessionCode}")
    ChatSession selectBySessionCode(String sessionCode);

    // 新建会话
    @Insert("INSERT INTO chatsession(chatSessionId, sessionCode, userId, toUserId, lastMessage, lastMessageTime, unreadCount, createTime) " +
            "VALUES(#{chatSessionId}, #{sessionCode}, #{userId}, #{toUserId}, #{lastMessage}, #{lastMessageTime}, #{unreadCount}, #{createTime})")
    int insertSession(ChatSession session);

    // 更新最后消息、未读+1
    @Update("UPDATE chatsession SET lastMessage=#{content}, lastMessageTime=#{time}, unreadCount=unreadCount+1 WHERE sessionCode=#{sessionCode}")
    int updateLastMessage(@Param("sessionCode") String sessionCode,
                          @Param("content") String content,
                          @Param("time") LocalDateTime time);
    @Update("UPDATE ChatSession SET lastMessage=#{content}, lastMessageTime=#{time} WHERE sessionCode=#{sessionCode}")
    void updateSessionWhenMessage(String sessionCode, String content, LocalDateTime time);

    // 自己是 userId恢复自己的删除状态
    @Update("UPDATE chatsession SET userIsDelete=0 WHERE sessionCode=#{sessionCode}")
    void resetUserDelete(String sessionCode);

    // 自己是 toUserId恢复自己的删除状态
    @Update("UPDATE chatsession SET toUserIsDelete=0 WHERE sessionCode=#{sessionCode}")
    void resetToUserDelete(String sessionCode);

    // 删除会话时 → 记录自己最后看到的消息ID
    @Update("UPDATE chatsession SET userIsDelete=1, userLastMsgId=#{msgId} WHERE sessionCode=#{sessionCode}")
    void deleteByUserId(String sessionCode, String msgId);

    @Update("UPDATE chatsession SET toUserIsDelete=1, toUserLastMsgId=#{msgId} WHERE sessionCode=#{sessionCode}")
    void deleteByToUserId(String sessionCode, String msgId);
}