package com.example.XSJ.Pojo.Menber;

import lombok.Data;

@Data
public class MemberSort {
    /**
     * 会员分类ID（主键）
     */
    private String memberSortId;

    /**
     * 会员等级
     */
    private String memberLevel;

    /**
     * 会员权限编码
     */
    private String memberPowerCode;

    /**
     * 权限数据
     */
    private String powerData;
}
