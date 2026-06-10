package com.example.XSJ.Pojo.VideoDTo;


import lombok.Data;

@Data
public class VideoFileDTO {


    private String videoResourcesId;
    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 文件格式（如：pdf、png）
     */
    private String resourceFormat;

    /**
     * 资源存储地址
     */
    private String resourceUrl;

    /**
     * 资源封面/缩略图Url
     */
    private String resourceThumUrl;

    /**
     * 资源大小（字节）
     */
    private Long resourceSize;

    /**
     * 下载数量
     */
    private Integer downloadCount;

    /**
     * 文件描述
     */
    private String resourceDesc;
}
