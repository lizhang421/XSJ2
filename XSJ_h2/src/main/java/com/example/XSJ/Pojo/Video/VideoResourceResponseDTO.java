package com.example.XSJ.Pojo.Video;

import com.example.XSJ.Pojo.VideoDTo.*;
import lombok.Data;
import java.util.List;

/**
 * 视频资源页面统一返回DTO
 * 封装前端所需的所有视频相关数据
 */
@Data
public class VideoResourceResponseDTO {
    /** 视频评论列表 */
    private List<CommentDTO> commentList;
    /** 页面渲染图片列表（HomeResourcesDTO） */
    private List<HomeResourcesDTO> homeResourceList;
    /** 视频相关文件列表 */
    private List<VideoFileDTO> videoFileList;
    /** 博主（发布者）信息列表 */
    private List<UserResourcesDTO> publisherInfoList;
    /** 推荐视频列表（同集合/同博主） */
    private List<VideoRecommendDTO> recommendVideoList;
}