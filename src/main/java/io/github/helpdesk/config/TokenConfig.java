package io.github.helpdesk.config;

import io.github.helpdesk.service.TokenService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "token")
public class TokenConfig {

    private TokenConfigProperties tokenConfigProperties;

    @Bean
    public TokenService passwordResetTokenService(final RedisTemplate<String, String> redisTemplate) {
        return new TokenService(tokenConfigProperties, redisTemplate);
    }

    public record TokenConfigProperties(String cachePrefix, Duration ttl) {
    }

}
