package com.eterlar.seckillsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 * @author eterlar
 */
@Configuration
public class RedisConfig {

    /**
     * 序列化 redis
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        //key 序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //value 序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //hash类型 key 的序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //hash类型 value序列化
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        //注入连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    /**
     * lua 脚本 Bean的配置
     * @return
     */
//    @Bean
//    public DefaultRedisScript<Long> redisScript() {
//        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
//        // 脚本的位置 和 application.yml 是同级目录
//        redisScript.setLocation(new ClassPathResource("stock.lua"));
//        redisScript.setResultType(Long.class);
//        return redisScript;
//    }
}
