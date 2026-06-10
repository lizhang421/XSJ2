package com.example.XSJ.ServiceImpl.Manager.Manager;

import com.example.XSJ.Pojo.Ai.AddAiConfig;
import com.example.XSJ.Pojo.Ai.AiConfig;
import com.example.XSJ.Pojo.Result;

public interface ManagerAiServiceImpl {
    Result getAi(AiConfig aiConfig);

    Result addAi(AddAiConfig addAiConfig);

    Result updataai(AiConfig aiConfig);

    Result deleteAi(String aiID, Integer status);
}
