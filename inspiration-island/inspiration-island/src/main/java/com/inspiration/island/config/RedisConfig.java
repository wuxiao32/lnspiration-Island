package com.inspiration.island.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 变量名统一 redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        // 1. 创建字符串序列化器
        StringRedisSerializer stringSer = new StringRedisSerializer();
        // 2. 创建JSON序列化器
        GenericJackson2JsonRedisSerializer jsonSer = new GenericJackson2JsonRedisSerializer();

        // 全部使用正确变量名 redisTemplate
        redisTemplate.setKeySerializer(stringSer);
        redisTemplate.setValueSerializer(jsonSer);
        redisTemplate.setHashKeySerializer(stringSer);
        redisTemplate.setHashValueSerializer(jsonSer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}