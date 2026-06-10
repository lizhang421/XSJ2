package com.example.XSJ.Pojo.UserChat;

import lombok.Data;
import java.util.List;

@Data
public class ReadMessageDTO {
    // 账户状态消息 ID 列表
    private List<String> accountStatusIds;
    // 审核记录消息 ID 列表
    private List<String> userReviewIds;
}