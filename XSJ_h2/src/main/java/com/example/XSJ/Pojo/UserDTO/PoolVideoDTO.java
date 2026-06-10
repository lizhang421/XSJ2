package com.example.XSJ.Pojo.UserDTO;

import com.example.XSJ.Pojo.Video.Video;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 返回池对应池和视频信息的类
 *
 */
@Data
public class PoolVideoDTO {
/**
 * 所属池id
 */
private String PoolId;
/**
 *池比例
 */
private BigDecimal TrafficRatio;
/**
 * 池名称
 */
private String PoolFullName;
/**
 *池类型
 */
private String PoolType;
/**
 * 池分组
 */
private String PoolCaetGory;
/**
 * 池抽取比例
 */
private BigDecimal GetPoolMount;

}
