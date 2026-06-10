package com.example.XSJ.Controller.Manager.System;


import com.alibaba.cloud.nacos.utils.StringUtils;

import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.System.HomeResources;
import com.example.XSJ.Pojo.UserChat.ReadMessageDTO;
import com.example.XSJ.Pojo.Video.VideoResourceResponseDTO;
import com.example.XSJ.Pojo.VideoDTo.*;

import com.example.XSJ.ServiceImpl.Manager.System.HomeResourcesImpl;
import com.example.XSJ.ServiceImpl.VideoServiceImpl.VideoServiceImpl;
import com.example.XSJ.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/home")
public class Resources {


    @Autowired
    private HomeResourcesImpl homeResourcesImpl;


    @Autowired
    private VideoServiceImpl videoServiceImpl;



    /**
     * 首页数据渲染
     * @return
     */
//  @GetMapping("/home")
//    public Result FirstHomeSourse(){
//    // 获取到用户的身份，只有当用户身份是1的时候，才有权力修改系统内容
//  }


    /**
     *   获取用户首页顶部轮播如图数据
     */
    @RequestMapping("/getHomeTop")
    public Result getHomeTop(){

        Integer isUse = 1;
        String recourseAddress="用户首页轮播区";
        List<HomeResourcesDTO> homeResources = homeResourcesImpl.getHoneTop(isUse,recourseAddress);
        if (homeResources != null){
            return Result.success(homeResources);
        }
        return Result.error();
    }


    /**
     * 1，进入这个页面刷新 获得这个页面需要渲染的信息
     * 2.获得这个视频的相关文件信息
     * 3.获得这个博主的相关信息
     * 4.获得这个博主的其他视频，如果属于群组优先返回群组消息
     * 5，返回评论区的内容（树形结构）
     * 获取Video 页面的渲染信息
     */
    @RequestMapping("/VideRecourse")
    public Result getVideoRecourse(
            @RequestParam(value = "Encode", required = true) String encode // 变量名规范：小写encode
    ) {
        try {

            VideoResourceResponseDTO responseDTO = new VideoResourceResponseDTO();


            List<CommentDTO> flatComments = videoServiceImpl.findVideoCommentsByCode(encode);
            List<CommentDTO> commentTree = buildCommentTree(flatComments); // 组装树形
            responseDTO.setCommentList(commentTree == null ? List.of() : commentTree); // 设置树形评论


            String recourseAddress = "Video";
            Integer isUse = 1;
            List<HomeResourcesDTO> homeResources = homeResourcesImpl.getHoneTop(isUse, recourseAddress);
            responseDTO.setHomeResourceList(homeResources == null ? List.of() : homeResources);


            List<VideoFileDTO> videoFiles = videoServiceImpl.findVideoFileByCode(encode);
            responseDTO.setVideoFileList(videoFiles == null ? List.of() : videoFiles);


            List<UserResourcesDTO> userResourcesDTOS = videoServiceImpl.findUserResourcesByCode(encode);
            responseDTO.setPublisherInfoList(userResourcesDTOS == null ? List.of() : userResourcesDTOS);


            List<VideoRecommendDTO> recommendDTOList = videoServiceImpl.findVideoTagByCode(encode);
            responseDTO.setRecommendVideoList(recommendDTOList == null ? List.of() : recommendDTOList);

            return Result.success(responseDTO);

        } catch (Exception e) {
            log.error("获取视频文件异常，enCode：{}", encode, e);
            return Result.error("系统繁忙，请稍后再试");
        }
    }

    /**
     * 组装评论树形结构：主评论 + 嵌套回复
     */
    private List<CommentDTO> buildCommentTree(List<CommentDTO> flatComments) {
        // 空值保护1：如果扁平列表为null，返回空列表
        if (flatComments == null) {
            return List.of();
        }

        // 空值保护2：过滤列表中的null元素，避免遍历时空指针
        List<CommentDTO> validComments = flatComments.stream()
                .filter(Objects::nonNull) // 关键：过滤掉null的CommentDTO对象
                .collect(Collectors.toList());

        List<CommentDTO> mainComments = validComments.stream()
                // 增强过滤条件：兼容parentId为null、空字符串、"0"等情况
                .filter(comment -> {
                    String parentId = comment.getParentId();
                    // 主评论判定：parentId为空/空字符串/纯空格/0
                    return parentId == null
                            || parentId.trim().isEmpty()
                            || "0".equals(parentId.trim());
                })
                // 空值保护3：排序前校验createTime非空
                .filter(comment -> comment.getCreateTime() != null)
                .sorted((c1, c2) -> c2.getCreateTime().compareTo(c1.getCreateTime()))
                .collect(Collectors.toList());

        // 2. 为每个主评论匹配回复，回复按创建时间升序
        for (CommentDTO mainComment : mainComments) {
            // 空值保护4：校验主评论ID非空
            String mainCommentId = mainComment.getVideoCommentId();
            if (StringUtils.isBlank(mainCommentId)) {
                mainComment.setReplyList(List.of());
                continue;
            }

            List<CommentDTO> replies = validComments.stream()
                    // 空值保护5：过滤回复的parentId非空，且匹配主评论ID
                    .filter(comment -> {
                        String replyParentId = comment.getParentId();
                        return StringUtils.isNotBlank(replyParentId)
                                && mainCommentId.equals(replyParentId.trim());
                    })

                    .filter(comment -> comment.getCreateTime() != null)
                    .sorted((c1, c2) -> c1.getCreateTime().compareTo(c2.getCreateTime()))
                    .collect(Collectors.toList());

            mainComment.setReplyList(replies);
        }

        return mainComments;
    }


    /*
    获取用户的审核信息和违禁信息
     */
    @GetMapping("/getUserAuditInfo")
    public Result getUserAuditInfo(
    ) {
        Map<String, Object>  map= ThreadLocalUtil.get();
        String UserEncode = map.get("EnCode").toString();
        try {
            Map<String, Object>au=new HashMap<>();
            au= videoServiceImpl.getUserAuditInfo(UserEncode);
            return Result.success(au);
        }catch (Exception e){
            log.error("获取用户审核信息异常",e);
            return Result.error();
        }

    }


    //用户已读系统信息状态
    @PostMapping("/readMessage")
    public Result readMessage(@RequestBody ReadMessageDTO dto) {
        try {
            videoServiceImpl.readMessage(dto);
            return Result.success("");
        }catch (Exception e){
            log.error("用户已读系统信息状态异常",e);
            return Result.error();
        }


    }


}



