package com.example.XSJ.Pojo.Menber;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

@Data

public class Member {

    /**
     * 会员ID（主键）
     */
    private String memberId;

    /**
     * 会员等级
     */
    private String memberLevel;

    /**
     * 会员价格
     */
    private BigDecimal memberPrice; // 若价格是数值类型，推荐用BigDecimal（避免精度丢失）

    /**
     * 会员编码
     */
    private String memberCode;

    /**
     * 权限类型
     */
    private String permDescTypes;

    /**
     * 会员描述
     */
    private String permDesc;

    /**
     * 创建时间
     */
    private Date createTime;
}
