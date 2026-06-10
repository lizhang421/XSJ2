package com.example.XSJ.Pojo.Video;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoResolutionDTO {

    private String EnCode;
    private String Resolution;
    private String VideoUrl;
    private Integer Width;
    private Integer Height;
    private Long Bitrate;
    private Long Size;
    private String Format;
    private LocalDateTime CreateTime;


}
