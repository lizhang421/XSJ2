package com.example.XSJ.ServiceImpl.Ai;

import com.example.XSJ.Pojo.Ai.AiConfigFrontDTO;
import com.example.XSJ.Pojo.Ai.UserAndAiDTO;

import java.util.List;

public interface AiServiceImpl {
    int getUserHone(String userEncode);

    void updateUserHone(String userEncode, int hone);

    List<AiConfigFrontDTO> getAiModel();

    List<UserAndAiDTO> getConversationList(String encode);
}
