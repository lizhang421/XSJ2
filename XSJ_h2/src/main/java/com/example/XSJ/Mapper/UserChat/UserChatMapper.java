package com.example.XSJ.Mapper.UserChat;

import com.example.XSJ.Pojo.UserChat.ChatSession;
import com.example.XSJ.Pojo.UserChat.FriendRelation;
import com.example.XSJ.Pojo.UserChat.FriendUserVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserChatMapper {

    @Insert("INSERT INTO friendrelation (" +
            "friendRelationId, userId, friendId, status, createTime, updateTime) " +
            "SELECT " +
            "CONCAT('UF', REPLACE(UUID(), '-', '')), " +
            "(SELECT userId FROM `user` WHERE Encode = #{userEncode}), " +
            "(SELECT userId FROM `user` WHERE Encode = #{friendEncode}), " +
            "3, NOW(), NOW() " +
            "FROM DUAL " +
            "WHERE NOT EXISTS (" +
            "   SELECT 1 FROM friendrelation " +
            "   WHERE userId = (SELECT userId FROM `user` WHERE Encode = #{userEncode}) " +
            "   AND friendId = (SELECT userId FROM `user` WHERE Encode = #{friendEncode}) " +
            ")")
    void addFriend(
            @Param("userEncode") String userEncode,
            @Param("friendEncode") String friendEncode
    );


    @Update("UPDATE friendrelation " +
            "SET status = #{status}, updateTime = NOW() " +
            "WHERE " +
            "   userId = (SELECT userId FROM `user` WHERE Encode = #{userEncode}) " +
            "AND friendId = (SELECT userId FROM `user` WHERE Encode = #{toUserEncode}) ")
    void agree(
            @Param("userEncode") String userEncode,
            @Param("toUserEncode") String toUserEncode,
            @Param("status") int status
    );


    @Select("SELECT " +
            "    u.EnCode, " +
            "    u.NickName, " +
            "    u.avatar, " +
            "    CASE " +
            // 如果当前用户是 userId → 显示 userRemark
            "        WHEN fr.userId = (SELECT userId FROM `user` WHERE EnCode = #{userEncode}) " +
            "        THEN fr.userRemark " +

            // 如果当前用户是 friendId 显示 friendRemark
            "        WHEN fr.friendId = (SELECT userId FROM `user` WHERE EnCode = #{userEncode}) " +
            "        THEN fr.friendRemark " +

            // 都没有就 NULL
            "        ELSE NULL " +
            "    END AS remark, " +
            "    fr.status " +
            "FROM friendrelation fr " +
            "JOIN `user` u ON " +
            "    (fr.userId = u.userId OR fr.friendId = u.userId) " +
            "WHERE " +
            "    (" +
            "        fr.userId = (SELECT userId FROM `user` WHERE EnCode = #{userEncode}) " +
            "        OR " +
            "        fr.friendId = (SELECT userId FROM `user` WHERE EnCode = #{userEncode}) " +
            "    ) " +
            "AND fr.status = 1 " +
            "AND u.EnCode != #{userEncode}")
    List<FriendUserVO> findFriendList(@Param("userEncode") String userEncode);

    @Select("SELECT " +
            "    u.EnCode, " +
            "    u.NickName, " +
            "    u.avatar, " +
            "    fr.remark, " +
            "    fr.status, " +
            "    fr.userId, " +
            "    fr.friendId " +
            "FROM friendrelation fr " +
            "JOIN `user` u ON " +
            "    fr.userId = u.userId OR fr.friendId = u.userId " +
            "WHERE " +
            "    (" +
            "        fr.userId = (SELECT userId FROM `user` WHERE EnCode = #{userEncode}) " +
            "        OR " +
            "        fr.friendId = (SELECT userId FROM `user` WHERE EnCode = #{userEncode}) " +
            "    ) " +
            "AND u.EnCode != #{userEncode} " +
            "ORDER BY fr.createTime DESC")
    List<FriendUserVO> getAllFriendRelation(@Param("userEncode") String userEncode);

    @Select("SELECT " +
            "    u.EnCode AS enCode, " +
            "    u.NickName AS nickName, " +
            "    u.avatar, " +
            "    fr.userRemark AS remark, " +    // 我给对方的备注
            "    fr.status " +                   // 关键：状态！
            "FROM friendrelation fr " +
            "JOIN `user` u ON fr.friendId = u.userId " +
            "WHERE fr.userId = #{myUserId}")
    List<FriendUserVO> getMySendApplyList(@Param("myUserId") String myUserId);

    @Select("SELECT " +
            "    u.EnCode AS enCode, " +
            "    u.NickName AS nickName, " +
            "    u.avatar, " +
            "    fr.friendRemark AS remark, " +  // 对方给我的备注
            "    fr.status " +                    // 关键：状态！
            "FROM friendrelation fr " +
            "JOIN `user` u ON fr.userId = u.userId " +
            "WHERE fr.friendId = #{myUserId}")
    List<FriendUserVO> getMyReceiveApplyList(@Param("myUserId") String myUserId);


    @Update("UPDATE friendrelation " +
            "SET status = 3, updateTime = NOW() " +
            "WHERE userId = (SELECT userId FROM `user` WHERE EnCode = #{userEncode}) " +
            "AND friendId = (SELECT userId FROM `user` WHERE EnCode = #{friendEncode})")
    void reApplyFriend(
            @Param("userEncode") String userEncode,
            @Param("friendEncode") String friendEncode
    );


    @Select("SELECT * FROM chatsession WHERE SessionCode = #{sessionCode}")
    ChatSession selectBySessionCode(String sessionCode);

    // 更新用户A最后可见消息ID
    @Update("UPDATE chatsession SET userLastMsgId = #{lastMsgId} WHERE sessionCode = #{sessionCode}")
    void updateUserLastMsgId(@Param("sessionCode") String sessionCode, @Param("lastMsgId") String lastMsgId);

    // 更新用户B最后可见消息ID
    @Update("UPDATE chatsession SET toUserLastMsgId = #{lastMsgId} WHERE sessionCode = #{sessionCode}")
    void updateToUserLastMsgId(@Param("sessionCode") String sessionCode, @Param("lastMsgId") String lastMsgId);


    @Update("UPDATE chatsession " +
            "SET UserIsDelete = #{isDelete}, " +
            "userLastMsgId = #{lastMsgId} " +
            "WHERE sessionCode = #{sessionCode}")
    void updateUserDeleteStatus(@Param("sessionCode") String sessionCode,
                                @Param("isDelete") Integer isDelete,
                                @Param("lastMsgId") String lastMsgId);

    /**
     * 更新接收方（对方）的删除状态 + 最后可见消息ID
     * @param sessionCode 会话唯一标识
     * @param isDelete 删除状态（1=已删，0=恢复）
     * @param lastMsgId 最后可见消息ID
     */
    @Update("UPDATE chatsession " +
            "SET ToUserIsDelete = #{isDelete}, " +
            "toUserLastMsgId = #{lastMsgId} " +
            "WHERE sessionCode = #{sessionCode}")
    void updateToUserDeleteStatus(@Param("sessionCode") String sessionCode,
                                  @Param("isDelete") Integer isDelete,
                                  @Param("lastMsgId") String lastMsgId);
}

