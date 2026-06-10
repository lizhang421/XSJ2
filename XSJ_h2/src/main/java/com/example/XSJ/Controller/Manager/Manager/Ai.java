package com.example.XSJ.Controller.Manager.Manager;


import com.example.XSJ.Pojo.Ai.AddAiConfig;
import com.example.XSJ.Pojo.Ai.AiConfig;
import com.example.XSJ.Pojo.Result;
import com.example.XSJ.ServiceImpl.Manager.Manager.ManagerAiServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/Ai")
public class Ai {

    @Autowired
    private ManagerAiServiceImpl managerAiServiceimpl;

    @RequestMapping("/getAi")
    public Result getAi(@RequestBody AiConfig aiConfig){
     try {
         Result result = managerAiServiceimpl.getAi(aiConfig);
         return result;
     }catch (Exception e){
         log.error("获取AI信息失败",e);
         return Result.error();
     }

    }
    @PostMapping("/addAi")
    public Result addAi(@RequestBody AddAiConfig AddAiConfig){
        try {
            Result result = managerAiServiceimpl.addAi(AddAiConfig);
            return result;
        }catch (Exception e){
            log.error("添加Ai模型失败",e);
            return Result.error();
        }

    }

    /**
     * 修改Ai模型
     */
    @PostMapping("/updateAi")
    public Result updateAi(@RequestBody AiConfig AiConfig){
        try {
            Result result = managerAiServiceimpl.updataai(AiConfig);
            return result;
        }catch (Exception e){
            log.error("修改Ai模型失败",e);
            return Result.error();
        }

    }
    @PostMapping("/deleteAi")
    public Result deleteAi(@RequestParam(value = "AiID",required = true) String AiID,
                           @RequestParam(value = "status",required = true) Integer status
                           ){
        try {
         Result result =   managerAiServiceimpl.deleteAi(AiID,status);
            return result;
        }catch (Exception e){
            log.error("删除Ai模型失败",e);
            return Result.error();
        }
    }


}
