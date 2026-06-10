package com.example.XSJ.service.Ai;

import com.example.XSJ.Mapper.Ai.AiMapper;
import com.example.XSJ.Pojo.Ai.AiConfigFrontDTO;
import com.example.XSJ.Pojo.Ai.UserAndAiDTO;
import com.example.XSJ.ServiceImpl.Ai.AiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiService implements AiServiceImpl {

    @Autowired
    private AiMapper aiMapper;


    @Override
    public int getUserHone(String userEncode) {
        return aiMapper.getuserhone(userEncode) ;
    }

    @Override
    public void updateUserHone(String userEncode, int hone) {
        aiMapper.updateuserhone(userEncode,hone);
    }

    @Override
    public List<AiConfigFrontDTO> getAiModel() {
        return aiMapper.getAiModel();
    }

    @Override
    public List<UserAndAiDTO> getConversationList(String encode) {
      return   aiMapper.getConversationList(encode);
    }
}
