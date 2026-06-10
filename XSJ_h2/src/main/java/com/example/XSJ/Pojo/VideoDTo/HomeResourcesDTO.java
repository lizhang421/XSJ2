package com.example.XSJ.Pojo.VideoDTo;

import lombok.Data;

@Data
public   class HomeResourcesDTO {
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
     * 资源应该展示的位置（如首页轮播/音频区等）
     */
    private String resourceAddress;
}
