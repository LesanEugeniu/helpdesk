package io.github.helpdesk.service;

import io.github.helpdesk.config.OtpConfig;
import io.github.helpdesk.util.OtpUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

public class OtpService {

    private final OtpConfig.OtpConfigProperties configProperties;

    private final RedisTemplate<String, String> redisTemplate;

    private final PasswordEncoder passwordEncoder;

    public OtpService(OtpConfig.OtpConfigProperties configProperties,
                      RedisTemplate<String, String> redisTemplate,
                      PasswordEncoder passwordEncoder) {
        this.configProperties = configProperties;
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateAndStoreOtp(final String userName) {
        final var otp = OtpUtil.generateOtp(configProperties.length());
        final var cacheKey = getCacheKey(userName);

        redisTemplate.opsForValue().set(cacheKey, passwordEncoder.encode(otp), configProperties.ttl());

        return otp;
    }

    public boolean isOtpValid(final String userName, final String otp) {
        final var cacheKey = getCacheKey(userName);

        return passwordEncoder.matches(otp, redisTemplate.opsForValue().get(cacheKey));
    }

    public void deleteOtp(final String userName) {
        final var cacheKey = getCacheKey(userName);

        redisTemplate.delete(cacheKey);
    }

    private String getCacheKey(String userName) {
        return configProperties.cachePrefix().formatted(userName);
    }

}
