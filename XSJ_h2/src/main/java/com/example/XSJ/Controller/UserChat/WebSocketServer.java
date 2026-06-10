package com.example.XSJ.Controller.UserChat;

import com.example.XSJ.Mapper.UserMapper.UserMapper;
import com.example.XSJ.Mapper.UserChat.ChatSessionMapper;
import com.example.XSJ.Pojo.UserChat.ChatMessage;
import com.example.XSJ.Pojo.UserChat.ChatMessageDTO;
import com.example.XSJ.Pojo.UserChat.ChatSession;
import com.example.XSJ.utils.SpringContextUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{userEncode}")
public class WebSocketServer {

    public static final Map<String, Session> SESSION_POOL = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    static {
        objectMapper.addMixIn(ChatMessageDTO.class, IgnoreUnknownPropertiesMixin.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IgnoreUnknownPropertiesMixin {}
    @OnOpen
    public void onOpen(Session session, @PathParam("userEncode") String userEncode) {

        session.setMaxTextMessageBufferSize(10 * 1024 * 1024);
        session.setMaxBinaryMessageBufferSize(10 * 1024 * 1024);

        SESSION_POOL.put(userEncode, session);
        sendOnlineStatus(userEncode, true);

        new Thread(() -> {
            try {
                Thread.sleep(100);
                sendAllOnlineUsersToNewUser(userEncode);
            } catch (Exception ignored) {}
        }).start();

        System.out.println("[WebSocket] 用户连接：" + userEncode);
    }

    @OnMessage
    public void onMessage(String messageJson,
                          Session session,
                          @PathParam("userEncode") String fromEncode) {
        System.out.println("==================================================");
        System.out.println("[WebSocket] 收到前端消息：" + messageJson);
        System.out.println("[WebSocket] 发送者fromEncode：" + fromEncode);

        try {
              ChatMessageDTO dto = objectMapper.readValue(messageJson, ChatMessageDTO.class);
            String toEncode = dto.getToEncode();
            String content = dto.getContent();
            Integer msgType = dto.getMsgType();

            if (msgType == null) {
                msgType = 0;
                System.out.println("[WebSocket] 前端未传msgType，默认设为0（文字）");
            }

            UserMapper userMapper = SpringContextUtil.getBean(UserMapper.class);
            String fromUserId = userMapper.getuserid(fromEncode);
            String toUserId = userMapper.getuserid(toEncode);

            String sessionCode = fromUserId.compareTo(toUserId) < 0 ?
                    fromUserId + "_" + toUserId :
                    toUserId + "_" + fromUserId;

            MongoTemplate mongoTemplate = SpringContextUtil.getBean(MongoTemplate.class);
            ChatMessage msg = new ChatMessage();
            msg.setSessionCode(sessionCode);
            msg.setFromUser(fromUserId);
            msg.setToUser(toUserId);
            msg.setContent(content);
            msg.setMsgType(msgType);
            msg.setIsRead(0);
            msg.setSendTime(LocalDateTime.now());
            msg.setDeletedByFrom(0);
            msg.setDeletedByTo(0);

            mongoTemplate.save(msg);
            System.out.println("[WebSocket] 消息已保存到MongoDB，消息ID：" + msg.get_id());

            String lastMsgShow = switch (msgType) {
                case 1 -> "[图片]";
                case 2 -> "[文件]";
                default -> content;
            };
            System.out.println("[WebSocket] 会话列表显示：" + lastMsgShow);
            ChatSessionMapper sessionMapper = SpringContextUtil.getBean(ChatSessionMapper.class);
            ChatSession exist = sessionMapper.selectBySessionCode(sessionCode);

            if (exist == null) {
                System.out.println("[WebSocket] 首次聊天，创建新会话");
                ChatSession newSession = new ChatSession();
                newSession.setChatSessionId(UUID.randomUUID().toString());
                newSession.setSessionCode(sessionCode);
                newSession.setUserId(fromUserId);
                newSession.setToUserId(toUserId);
                newSession.setLastMessage(lastMsgShow);
                newSession.setLastMessageTime(LocalDateTime.now());
                newSession.setUnreadCount(1);
                newSession.setUserIsDelete(0);
                newSession.setToUserIsDelete(0);
                newSession.setUserLastMsgId(msg.get_id() != null ? msg.get_id().toString() : "");
                newSession.setToUserLastMsgId(msg.get_id() != null ? msg.get_id().toString() : "");
                newSession.setCreateTime(LocalDateTime.now());
                sessionMapper.insertSession(newSession);
            } else {
                System.out.println("[WebSocket] 更新已有会话");
                sessionMapper.updateSessionWhenMessage(sessionCode, lastMsgShow, LocalDateTime.now());
                if (fromUserId.equals(exist.getUserId())) {
                    sessionMapper.resetUserDelete(sessionCode);
                } else {
                    sessionMapper.resetToUserDelete(sessionCode);
                }
            }


            Map<String, Object> result = new HashMap<>();
            result.put("msgId", msg.get_id().toString());
            result.put("fromEncode", fromEncode);
            result.put("toEncode", toEncode);
            result.put("content", content);
            result.put("sendTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            result.put("msgType", msgType);
            result.put("isRead", 0);

            String sendJson = objectMapper.writeValueAsString(result);
            System.out.println("[WebSocket] 推送消息给双方：" + sendJson.substring(0, 80) + "...");

            sendToUser(toEncode, sendJson);
            sendToUser(fromEncode, sendJson);

            System.out.println("[WebSocket] 消息处理完成 ✅");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("[WebSocket] 消息处理失败 ❌");
            System.err.println("[WebSocket] 错误信息：");
            e.printStackTrace();
            System.out.println("==================================================");
        }
    }

    // ====================== 发送消息给用户 ======================
    public static void sendToUser(String encode, String message) {
        Session session = SESSION_POOL.get(encode);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                System.out.println("[WebSocket] 已发送给：" + encode);
            } catch (IOException e) {
                System.err.println("[WebSocket] 发送失败给：" + encode);
                e.printStackTrace();
            }
        } else {
            System.out.println("[WebSocket] 用户不在线：" + encode);
        }
    }

    // ====================== 断开连接 ======================
    @OnClose
    public void onClose(@PathParam("userEncode") String userEncode) {
        sendOnlineStatus(userEncode, false);
        SESSION_POOL.remove(userEncode);
        System.out.println("[WebSocket] 用户断开：" + userEncode);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("[WebSocket] 发生错误");
        error.printStackTrace();
    }

    // ====================== 在线状态 ======================
    private void sendOnlineStatus(String userEncode, boolean isOnline) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("type", "onlineStatus");
            data.put("userEncode", userEncode);
            data.put("isOnline", isOnline);
            String msg = objectMapper.writeValueAsString(data);

            Set<String> keys = new HashSet<>(SESSION_POOL.keySet());
            for (String key : keys) {
                if (key.equals(userEncode)) continue;
                sendToUser(key, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendAllOnlineUsersToNewUser(String newUserEncode) {
        try {
            List<Map<String, Object>> onlineList = new ArrayList<>();
            Set<String> keys = new HashSet<>(SESSION_POOL.keySet());

            for (String encode : keys) {
                if (encode.equals(newUserEncode)) continue;
                Map<String, Object> item = new HashMap<>();
                item.put("userEncode", encode);
                item.put("isOnline", true);
                onlineList.add(item);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("type", "allOnlineStatus");
            result.put("onlineList", onlineList);

            String json = objectMapper.writeValueAsString(result);
            sendToUser(newUserEncode, json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendReadStatus(String toUserEncode, List<String> msgIds, String fromEncode) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("type", "readStatus");
            map.put("fromEncode", fromEncode);
            map.put("msgIds", msgIds);
            String json = objectMapper.writeValueAsString(map);
            sendToUser(toUserEncode, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}