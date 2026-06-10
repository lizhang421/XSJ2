package com.example.XSJ.utils.Video;

import com.example.XSJ.Pojo.UserDTO.PoolVideoDTO;
import com.example.XSJ.Pojo.Video.Video;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VideoCache {

    /**
     * 池与赤贫对应的缓存
     */

    public Cache<String, List<Video>> poolVideoCache = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES) //写入一小时后过期
            .maximumSize(1000000) // 缓存最大容量1000000
            .build();
    /**
     * 视频池缓存
     * key 为简单类型 高考 ，数学 ，英语
     * value 是json 序列化之后的数据
     */

    public Cache<String,String> poolcache=Caffeine.newBuilder()
            .expireAfterWrite(60,TimeUnit.MINUTES)
            .maximumSize(1000000)
            .build();
    /**
     * key是 视频分类加池类型拼接起来的
     * 配置规则缓存
     * 这个缓存记录池中的配置规则
     * json序列话结果
     */
    public Cache<String,String> PoolExtractRule=Caffeine.newBuilder()
            .expireAfterWrite(300,TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();




}
