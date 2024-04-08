package gg.garen.back.chatting.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gg.garen.back.chatting.dto.ChatDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    // Redis와 연결 설정
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, ChatDto> redisMessageTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ChatDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 직렬화를 위한 Jackson2JsonRedisSerializer 설정
        Jackson2JsonRedisSerializer<ChatDto> serializer = new Jackson2JsonRedisSerializer<>(ChatDto.class);

        // 키에 대한 직렬화 방식은 StringRedisSerializer 사용
        template.setKeySerializer(new StringRedisSerializer());

        // 값(Value)에 대한 직렬화 방식으로 Jackson2JsonRedisSerializer 사용
        template.setValueSerializer(serializer);

        // 해시 키와 값에 대해서도 동일한 직렬화 방식 적용
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }

}