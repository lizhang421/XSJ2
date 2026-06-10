package com.example.XSJ.Pojo.VideoDTo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentSubmitDTO {


    @NotBlank(message = "视频编码不能为空")
    private String enCode;

    // 评论内容：必传+长度限制
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过500字")
    private String comment;

    // 父评论ID：可选（根评论不传）
    private String parentId;


}
