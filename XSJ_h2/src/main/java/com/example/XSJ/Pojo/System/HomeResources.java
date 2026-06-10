package com.example.XSJ.Pojo.System;

import lombok.Data;

import java.util.Date;

/**
 * 资源表POJO类
 * 对应数据库表：HomeResources
 */
@Data
public class HomeResources {

    /**
     * 首页资源主键id
     */
    private String homeResourcesId;

    /**
     * 资源类型
     */
    private String type;

    /**
     * 文件名字
     */
    private String fileName;

    /**
     * 文件存储的阿里云地址
     */
    private String address;

    /**
     * 资源是否启用（0=禁用，1=启用）
     */
    private Integer isUse;

    /**
     * 资源应该展示的位置（如首页轮播/音频区等）
     */
    private String resourceAddress;

    /**
     * 上传人id
     */
    private String uploadPeople;

    /**
     * 资源上传时间（创建时自动填充）
     */
    private Date uploadTime;

    /**
     * 资源更新时间
     */
    private Date updateTime;
    /**
     * 跳转路径
     */
    private  String  jumUrl;
    /**
     * 图片资源长度
     */
    private  int  longer;
    /**
     * 图片资源宽度
     */
    private  int  wide;
}