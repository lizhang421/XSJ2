package com.example.XSJ.Pojo.Video;

import lombok.Data;
import java.util.Date;

@Data
public class VideoGroup {
    /** 系列组ID */
    private String groupId;
    /** 系列组名称 */
    private String groupName;
    /** 系列描述 */
    private String groupDesc;
    /** 系列封面URL */
    private String groupCoverUrl;
    /** 系列状态：0-正常 1-禁用 2-删除 */
    private Integer groupStatus;

    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
    /** 创建人 */
    private String createUser;
    /** 更新人 */
    private String updateUser;
}