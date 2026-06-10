package com.example.XSJ.Controller.Ai;


import com.example.XSJ.Pojo.Ai.*;
import com.example.XSJ.Pojo.Result;
import com.example.XSJ.ServiceImpl.Ai.AiServiceImpl;
import com.example.XSJ.service.Ai.AiChatService;
import com.example.XSJ.utils.ThreadLocalUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiServiceImpl aiService;


    @Resource
    private AiChatService aiChatService;


    @Autowired
    @Qualifier("qwen1")
    private ChatClient chatClient1;

    @GetMapping(value = "/chat1",  produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    public Flux<String> chat1(@RequestParam String input){


      return  chatClient1.prompt()
              .user(input)
              .stream()
              .content();
    }

    //获得用户选择的ai界面的样式
    @GetMapping("/hone")
    public Result hone(){
        //从线程中获取与当前的额细腻些
        Map<String,Object> map= ThreadLocalUtil.get();
        String userEncode = map.get("EnCode").toString();
        try {

            int hone= aiService.getUserHone(userEncode);
            return Result.success(hone);

        }catch (Exception e){
            log.warn("获取用户 ai 样式失败");
            return Result.error(1);
        }
    }
    @PostMapping("/hone2")
    public Result home2(
            @RequestParam(value = "hone",required = true) int hone
    ){
        Map<String,Object> map= ThreadLocalUtil.get();
        String userEncode = map.get("EnCode").toString();
    try {
        aiService.updateUserHone(userEncode,hone);
    }catch (Exception e){
        log.warn("修改用户ai样式失败");
        return Result.error(1);
    }
    return Result.success();
    }

    //获得全部的AI模型信息返回给前端渲染s
    @GetMapping("/ai-model")
    public Result getAiModel(){
        try {
            List<AiConfigFrontDTO> aiModelFrontDTOS= aiService.getAiModel();
            return Result.success(aiModelFrontDTOS);
        }catch (Exception e){
            log.warn("获取ai模型失败");
            return Result.error();
        }
    }


    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AiChatStreamResponse> streamChat(@Valid @RequestBody AiChatRequest request) {
        return aiChatService.streamChatWithAiFlux(request);
    }

    @GetMapping("/chat/history")
    public Result getChatHistory(
            @RequestParam String sessionId) { // 会话ID
        try {
            List<AiChatRecord>  history=aiChatService.getChatHistory( sessionId);
            return Result.success(history);
        }catch (Exception e){
            log.warn("获取历史记录失败");
            return Result.error();
        }
    }

    //获取历史会话列表
    @GetMapping("/chat/conversation")
    public Result getConversationList(){
        Map<String,Object> map= ThreadLocalUtil.get();
        String encode = map.get("EnCode").toString();

        try {
         List<UserAndAiDTO>userAndAiDTOS = aiService.getConversationList(encode);
         return Result.success(userAndAiDTOS);
        }catch (Exception e){
            log.warn("获取历史会话列表失败");
            return Result.error();
        }


    }











}
