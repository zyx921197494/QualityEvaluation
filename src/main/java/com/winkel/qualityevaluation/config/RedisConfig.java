package com.winkel.qualityevaluation.config;
/*
  @ClassName RedisConfig
  @Description
  @Author winkel
  @Date 2022-03-30 21:21
  */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    @Primary
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

}
