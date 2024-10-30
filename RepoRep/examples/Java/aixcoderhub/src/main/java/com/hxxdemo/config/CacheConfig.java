package com.hxxdemo.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author wyb
 * @date 2024/4/11
 */

@Configuration
public class CacheConfig {
    @Bean
    public Cache<String, Object> guavaCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(1000) // 设置缓存的最大容量
                .expireAfterWrite(1, TimeUnit.MINUTES) // 设置缓存在写入10分钟后失效
                .build();
    }
}
