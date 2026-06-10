package com.example.XSJ.service.UserChat;

import com.example.XSJ.Pojo.Result;
import org.bson.types.ObjectId;
import com.example.XSJ.Controller.UserChat.WebSocketServer;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Query;
import com.example.XSJ.Mapper.UserChat.UserChatMapper;
import com.example.XSJ.Mapper.UserMapper.UserMapper;
import com.example.XSJ.Pojo.UserChat.*;
import com.example.XSJ.ServiceImpl.USerChat.UserChatServiceImpl;
import com.example.XSJ.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserChatService implements UserChatServiceImpl {

    @Autowired
    private UserChatMapper userChatMapper;


    @Autowired
    private UserMapper userMapper;


    private final MongoTemplate mongoTemplate; // 注入MongoTemplate

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void addFriend(String userEncode, String toUserEncode) {


        userChatMapper.addFriend(userEncode, toUserEncode);

        userChatMapper.reApplyFriend(userEncode, toUserEncode);

    }

    @Override
    public void agree(String userEncode, String toUserEncode, int status) {
        userChatMapper.agree(toUserEncode, userEncode, status);
    }

    @Override
    public List<FriendUserVO> frindlist(String userEncode) {
        return userChatMapper.findFriendList(userEncode);
    }

    @Override
    public ApplyFriendDTO applylist(String userEncode) {

        String myUserId = userMapper.getuserid(userEncode);

        List<FriendUserVO> sendList = userChatMapper.getMySendApplyList(myUserId);

        List<FriendUserVO> receiveList = userChatMapper.getMyReceiveApplyList(myUserId);


        ApplyFriendDTO dto = new ApplyFriendDTO();
        dto.setFriendapplyUserVOList(receiveList);  // 别人申请我
        dto.setUserapplyfriedVOList(sendList);     // 我申请别人

        return dto;
    }

    public List<ChatMessageVO> chatHistory(String toUserEncode) {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userEncode = (String) map.get("EnCode");
        String myUserId = userMapper.getuserid(userEncode);
        String toUserId = userMapper.getuserid(toUserEncode);

        String sessionCode = myUserId.compareTo(toUserId) < 0
                ? myUserId + "_" + toUserId
                : toUserId + "_" + myUserId;

        ChatSession chatSession = userChatMapper.selectBySessionCode(sessionCode);
        if (chatSession == null) {
            return List.of();
        }

        boolean isFromUser = myUserId.equals(chatSession.getUserId());
        boolean isSessionDeleted = isFromUser
                ? chatSession.getUserIsDelete() == 1
                : chatSession.getToUserIsDelete() == 1;

        Criteria criteria = Criteria.where("sessionCode").is(sessionCode);


        if (isFromUser) {

            criteria.and("deletedByFrom").is(0);
        } else {

            criteria.and("deletedByTo").is(0);
        }


        if (isSessionDeleted) {
            String lastMsgId = isFromUser
                    ? chatSession.getUserLastMsgId()  // 发起者的最后消息ID
                    : chatSession.getToUserLastMsgId(); // 接收者的最后消息ID

            if (lastMsgId != null && !lastMsgId.isEmpty() && ObjectId.isValid(lastMsgId)) {
                criteria.and("_id").gt(new ObjectId(lastMsgId));
            }
        }

        Query query = new Query(criteria);
        query.with(Sort.by(Sort.Direction.ASC, "sendTime")); // 按时间正序

        List<ChatMessage> messageList = mongoTemplate.find(query, ChatMessage.class, "chat_message");
        List<ChatMessageVO> result = new ArrayList<>();
        for (ChatMessage msg : messageList) {
            ChatMessageVO vo = new ChatMessageVO();
            vo.setMsgId(msg.getId().toString());
            vo.setContent(msg.getContent());
            vo.setSendTime(msg.getSendTime().format(FORMATTER));
            vo.setMsgType(msg.getMsgType());
            vo.setIsRead(msg.getIsRead());
            vo.setFromEncode(msg.getFromUser().equals(myUserId) ? userEncode : toUserEncode);
            vo.setToEncode(msg.getToUser().equals(myUserId) ? userEncode : toUserEncode);
            result.add(vo);
        }

        return result;
    }



    @Override
    public void read(List<String> messageIds, String toUserEncode) {
        Map<String, Object> map = ThreadLocalUtil.get();
        String myEncode = (String) map.get("EnCode");       // 我的encode
        String myUserId = userMapper.getuserid(myEncode);   // 我的userId

        // 2. 构建查询条件：只能【已读发给我的、未读的消息】
        Query query = new Query(
                Criteria.where("_id").in(messageIds)
                        .and("toUser").is(myUserId)    // 消息是发给我的
                        .and("isRead").is(0)           // 只改未读
        );

        // 3. 更新为已读
        Update update = Update.update("isRead", 1);
        mongoTemplate.updateMulti(query, update, ChatMessage.class, "chat_message");

        WebSocketServer.sendReadStatus(toUserEncode, messageIds,toUserEncode);


    }

    @Override
    public void deleteMessages(String toUserEncode, List<String> msgIdList) {
        Map<String, Object> map = ThreadLocalUtil.get();
        String myEncode = (String) map.get("EnCode");
        String myUserId = userMapper.getuserid(myEncode);
        String toUserId = userMapper.getuserid(toUserEncode);

        // 拼接 sessionCode（和历史消息一致）
        String sessionCode = myUserId.compareTo(toUserId) < 0
                ? myUserId + "_" + toUserId
                : toUserId + "_" + myUserId;

        ChatSession session = userChatMapper.selectBySessionCode(sessionCode);
        if (session == null) return;

        boolean iAmOwner = myUserId.equals(session.getUserId());
        String deleteField = iAmOwner ? "deletedByFrom" : "deletedByTo";
        String lastMsgIdField = iAmOwner ? "userLastMsgId" : "toUserLastMsgId";


        Query query = new Query(Criteria.where("_id").in(msgIdList)
                .and("sessionCode").is(sessionCode));

        Update update = new Update();
        update.set(deleteField, 1);

        mongoTemplate.updateMulti(query, update, ChatMessage.class);

        Query lastQuery = new Query(Criteria.where("sessionCode").is(sessionCode));
        lastQuery.with(Sort.by(Sort.Direction.DESC, "sendTime"));
        lastQuery.limit(1);
        ChatMessage lastMsg = mongoTemplate.findOne(lastQuery, ChatMessage.class);

        if (lastMsg == null) return;
        String lastMsgId = lastMsg.getId().toString();

        // =========================================
        // 更新会话：记录最后一条可见消息ID
        // =========================================
        if (iAmOwner) {
            userChatMapper.updateUserLastMsgId(sessionCode, lastMsgId);
        } else {
            userChatMapper.updateToUserLastMsgId(sessionCode, lastMsgId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteSession(String toUserEncode) {
        // 1. 从ThreadLocal获取当前登录用户的Encode（会话信息）
        Map<String, Object> map = ThreadLocalUtil.get();
        String myEncode = (String) map.get("EnCode");

        // 2. 根据Encode查询双方的UserId（和你原有逻辑完全一致）
        String myUserId = userMapper.getuserid(myEncode);
        String toUserId = userMapper.getuserid(toUserEncode);

        // 3. 生成唯一会话sessionCode（按UserId字典序拼接，保证双向一致）
        String sessionCode = myUserId.compareTo(toUserId) < 0
                ? myUserId + "_" + toUserId
                : toUserId + "_" + myUserId;

        // 4. 查询会话是否存在
        ChatSession session = userChatMapper.selectBySessionCode(sessionCode);
        if (session == null) {
           return Result.error("会话不存在");
        }

        // 5. 判断当前用户是会话的发起方(UserId)还是接收方(ToUserId)
        boolean iAmFromUser = myUserId.equals(session.getUserId());

        // 6. 核心：更新会话删除状态 + 最后可见消息ID
        if (iAmFromUser) {
            // 我是发起方：标记我已删除，更新我的最后可见消息ID
            // 先查当前会话的最后一条消息
            Query lastMsgQuery = new Query(Criteria.where("sessionCode").is(sessionCode))
                    .with(Sort.by(Sort.Direction.DESC, "sendTime"))
                    .limit(1);
            ChatMessage lastMsg = mongoTemplate.findOne(lastMsgQuery, ChatMessage.class);

            String lastMsgId = lastMsg != null ? lastMsg.getId().toString() : null;
            // 更新：我已删除，同时记录我看到的最后一条消息（恢复时用）
            userChatMapper.updateUserDeleteStatus(sessionCode, 1, lastMsgId);
        } else {
            // 我是接收方：标记我已删除，更新对方的最后可见消息ID
            Query lastMsgQuery = new Query(Criteria.where("sessionCode").is(sessionCode))
                    .with(Sort.by(Sort.Direction.DESC, "sendTime"))
                    .limit(1);
            ChatMessage lastMsg = mongoTemplate.findOne(lastMsgQuery, ChatMessage.class);

            String lastMsgId = lastMsg != null ? lastMsg.getId().toString() : null;
            // 更新：我已删除，同时记录我看到的最后一条消息（恢复时用）
            userChatMapper.updateToUserDeleteStatus(sessionCode, 1, lastMsgId);
        }


        Query msgQuery = new Query(Criteria.where("sessionCode").is(sessionCode));
        Update msgUpdate = new Update();
        if (iAmFromUser) {
            msgUpdate.set("deletedByFrom", 1);
        } else {
            msgUpdate.set("deletedByTo", 1);
        }
        mongoTemplate.updateMulti(msgQuery, msgUpdate, ChatMessage.class);
        return null;
    }


}

