package com.example.XSJ.service.Manager.Manager;


import com.example.XSJ.Mapper.System.ManagerAiMapper;
import com.example.XSJ.Pojo.Ai.AddAiConfig;
import com.example.XSJ.Pojo.Ai.AiConfig;
import com.example.XSJ.Pojo.Result;
import com.example.XSJ.ServiceImpl.Manager.Manager.ManagerAiServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class ManagerAiService implements ManagerAiServiceImpl {

    @Autowired
    private ManagerAiMapper managerAiMapper;

    @Override
    public Result getAi(AiConfig aiConfig) {
        try {

            int pageNum = aiConfig.getPageNum();
            int pageSize = aiConfig.getPageSize();
            int start = (pageNum - 1) * pageSize;

             List<AiConfig> aiConfigList =managerAiMapper.selectAiConfigList(aiConfig, start);
             return Result.success(aiConfigList);
        }catch (Exception e){
            log.error("获取AI信息失败",e);
            return Result.error();
        }
    }
    @Override
    public Result addAi(AddAiConfig addAiConfig) {
        try {
            String AiID="AI_"+ UUID.randomUUID().toString();
            managerAiMapper.addAi(addAiConfig,AiID);
            return Result.success("AI模型添加成功");
        }catch (Exception e){
            log.error("添加AI信息失败",e);
            return Result.error();
        }

    }

    @Override
    public Result updataai(AiConfig aiConfig) {
        try {
            managerAiMapper.updataai(aiConfig);
            return Result.success("修改AI信息成功");
        }catch (Exception e){
            log.info("修改AI信息失败",e);
            return Result.error();
        }
    }

    @Override
    public Result deleteAi(String aiID, Integer status) {

        try {
            if (aiID==null|| aiID.equals("")){
                return Result.error("请选择要删除的AI模型");
            }
            managerAiMapper.deleteAi(aiID,status);

        }catch (Exception e){

        }
        return null;
    }
}
