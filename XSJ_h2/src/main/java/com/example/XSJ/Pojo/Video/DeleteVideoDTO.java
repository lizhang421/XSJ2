package com.example.XSJ.Pojo.Video;

import lombok.Data;

import java.util.List;

@Data
public class DeleteVideoDTO {
    private String encode;
    private List<String> videoAuditTagId;
    private Integer status;
}