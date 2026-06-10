package com.example.XSJ.Controller.UserChat;


import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.UserChat.ApplyFriendDTO;
import com.example.XSJ.Pojo.UserChat.ChatMessageVO;
import com.example.XSJ.Pojo.UserChat.FriendUserVO;
import com.example.XSJ.ServiceImpl.USerChat.UserChatServiceImpl;
import com.example.XSJ.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/userChat")
public class UserChat {

    @Autowired
    private UserChatServiceImpl userChatService;



    /**
     * 申请添加好友
     * @param toUserEncode
     * @return
     */
    @PostMapping("/addFriend")
    public Result addFriend(@Param("toUserEncode") String toUserEncode){
        //从当前线程中获取用户的登录信息
        Map<String,Object> map= ThreadLocalUtil.get();
        String userEncode=(String) map.get("EnCode");
        if (toUserEncode== null|| toUserEncode.trim().isEmpty()){
            return Result.error("账号不存在");
        }
        if (toUserEncode.equals(userEncode)){
            return Result.error("不能添加自己为好友");
        }

        try {
            userChatService.addFriend(userEncode,toUserEncode);
            return  Result.success("添加成功");
        }catch (Exception e){
            log.error("添加好友失败",e);
            return Result.error();
        }

    }


    @PostMapping("/agree")
    public  Result agree(
            @Param("userEncode") String toUserEncode,
            @Param("status") int status
    ) {
        //1是同意 2 是删除 3是待确认，4是不同意，
        Map<String,Object> map= ThreadLocalUtil.get();
        String userEncode=(String) map.get("EnCode");
        if (toUserEncode== null|| toUserEncode.trim().isEmpty()){
            return Result.error("账号不存在");
        }
        if (toUserEncode.equals(userEncode)){
            return Result.error("不能添加自己为好友");
        }
        try {
            userChatService.agree(userEncode,toUserEncode,status);
            return  Result.success("添加成功");
        }catch (Exception e){
            log.error("添加好友失败",e);
            return Result.error();
        }


    }


    @GetMapping("/frindlist")
    public  Result frindlist()
    {
        Map<String,Object> map= ThreadLocalUtil.get();
        String userEncode=(String) map.get("EnCode");

        try {
            List<FriendUserVO> list = userChatService.frindlist(userEncode);
            return Result.success(list);
        }catch (Exception e){
            log.error("添加好友失败",e);
            return Result.error();
        }


    }


    //获取用户申请好友或者申请用户为好友的记录
    @GetMapping("/applylist")
    public  Result applylist()
    {

        Map<String, Object> map = ThreadLocalUtil.get();
        String userEncode = (String) map.get("EnCode");
        if (userEncode == null || userEncode.isBlank()) {
            return Result.error("用户未登录");
        }

        try {
            ApplyFriendDTO applyList = userChatService.applylist(userEncode);
            return Result.success(applyList);
        } catch (Exception e) {
            log.error("查询好友申请列表异常", e);
            return Result.error("查询好友申请失败，请稍后重试");
        }

    }

    /**
     * 获取用户和某个用户的聊天记录一进入聊天历史之后刷新
     */
    @GetMapping("/chatHistory")
    public Result chatHistory(@Param("toUserEncode") String toUserEncode) {
        if (toUserEncode == null || toUserEncode.isBlank()) {
            return Result.error("用户已删除");
        }
        try {
           List<ChatMessageVO>list=userChatService.chatHistory(toUserEncode);
            return Result.success(list);
        }catch (Exception e){
            log.error("查询聊天记录异常", e);
            return Result.error("查询聊天记录失败，请稍后重试");
        }
    }


    /**
     * 信息批量已读的功能
     */

    @PostMapping("/chat/read")
    public Result read(
            @RequestBody List<String> messageIds,
            @RequestParam String toUserEncode  // 前端直接传对方encode
    ) {
        userChatService.read(messageIds, toUserEncode);
        return Result.success("已读成功");
    }


    /**
     * 用户删除会话
     */

    @PostMapping("/chat/deleteMessages")
    public Result deleteMessages(
            @RequestParam String toUserEncode,
            @RequestBody List<String> msgIdList) {

       try {
           userChatService.deleteMessages(toUserEncode, msgIdList);
           return Result.success();
       }catch (Exception e)
       {
           log.error("删除消息异常",e);
           return Result.error("删除消息失败，请稍后重试");
       }
    }

    @DeleteMapping("/chat/deleteSession")
    public Result deleteSession(
            @RequestParam String toUserEncode) {
        try {
            Result result=userChatService.deleteSession(toUserEncode);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除会话异常", e);
            return Result.error("删除会话失败，请稍后重试");
        }
    }




















}
