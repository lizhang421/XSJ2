package com.example.XSJ.Config;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;

@Configuration
@EnableCaching  //开启cache注解支持
public class EmailCacheConfig {


    /**
     * 配置验证码缓存管理器
     * 工厂
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.MINUTES) // 验证码2分钟过期
                .maximumSize(1000) // 最大缓存1000条（防止内存溢出）
                .recordStats(); //记录缓存统计信息（命中率等）

        // 设置缓存名称
        caffeineCacheManager.setCacheNames(Arrays.asList("login", "register", "forget:pwd:email:code"));
        return caffeineCacheManager;
    }

}
