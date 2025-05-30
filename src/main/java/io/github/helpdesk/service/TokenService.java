package io.github.helpdesk.service;

import io.github.helpdesk.config.TokenConfig;
import io.github.helpdesk.util.TokenUtil;
import org.springframework.data.redis.core.RedisTemplate;

public class TokenService {

    private final TokenConfig.TokenConfigProperties tokenConfigProperties;

    private final RedisTemplate<String, String> redisTemplate;


    public TokenService(TokenConfig.TokenConfigProperties tokenConfigProperties,
                        RedisTemplate<String, String> redisTemplate) {
        this.tokenConfigProperties = tokenConfigProperties;
        this.redisTemplate = redisTemplate;
    }

    public String generateAndStoreToken(final String email) {
        final String token = TokenUtil.generateUUID().toString();
        final String cacheKey = getCacheKey(token);

        redisTemplate.opsForValue().set(cacheKey, email, tokenConfigProperties.ttl());
        return token;
    }

    public String getEmail(final String token) {
        return redisTemplate.opsForValue().get(getCacheKey(token));
    }

    public void deleteToken(final String token) {
        final String cacheKey = getCacheKey(token);
        redisTemplate.delete(cacheKey);
    }

    private String getCacheKey(String token) {
        return tokenConfigProperties.cachePrefix().formatted(token);
    }

}
