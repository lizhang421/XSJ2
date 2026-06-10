package com.example.XSJ.Pojo.UserChat;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "chat_message")
public class ChatMessage {

    @Id
    private ObjectId _id;

    private String sessionCode;
    private String fromUser;
    private String toUser;
    private String content;
    private Integer msgType;  // 0=文字 1=图片 2=文件
    private Integer isRead;
    private LocalDateTime sendTime;

    private Integer deletedByFrom = 0;  // 发送者删除
    private Integer deletedByTo = 0;    // 接收者删除

    public ObjectId getId() {
        return _id;
    }
}