package io.stageclear.common.config;

import io.stageclear.common.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * StageClear 公共模块的 Spring Boot 自动配置
 * <p>
 * 当任意服务（如 customer-core / 未来的 user-service / agent-service / kb-service）
 * 引入 stageclear-common 后，
 * Spring Boot 启动时会自动加载本类，把 common 模块里的 Bean 装进容器。
 * <p>
 * 加载机制见同目录下的：
 * META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 */
@AutoConfiguration
@Import({TraceIdFilter.class, GlobalExceptionHandler.class})
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class CommonAutoConfiguration {

    /**
     * 自定义 RedisTemplate：key 用 String，value 用 JSON
     * Spring Boot 默认只配 StringRedisTemplate，业务里要存对象必须自己定义
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> tpl = new RedisTemplate<>();
        tpl.setConnectionFactory(factory);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        tpl.setKeySerializer(stringSerializer);
        tpl.setValueSerializer(jsonSerializer);
        tpl.setHashKeySerializer(stringSerializer);
        tpl.setHashValueSerializer(jsonSerializer);
        tpl.afterPropertiesSet();
        return tpl;
    }
}
