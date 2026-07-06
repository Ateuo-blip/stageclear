package io.stageclear.common.config;

import io.stageclear.common.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * StageClear 公共模块的 Spring Boot 自动配置
 * <p>
 * 当任意服务（如 portal / auth / gateway）引入 stageclear-common 后，
 * Spring Boot 启动时会自动加载本类，把 common 模块里的 Bean 装进容器。
 * <p>
 * 加载机制见同目录下的：
 * META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 */
@AutoConfiguration
@Import({TraceIdFilter.class, GlobalExceptionHandler.class})
public class CommonAutoConfiguration {

    /**
     * 自定义 RedisTemplate：key 用 String，value 用 JSON
     * Spring Boot 默认只配 StringRedisTemplate，业务里要存对象必须自己定义
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> tpl = new RedisTemplate<>();
        tpl.setConnectionFactory(factory);
        tpl.setKeySerializer(new StringRedisSerializer());
        tpl.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return tpl;
    }
}