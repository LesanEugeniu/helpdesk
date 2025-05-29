package io.github.helpdesk.config;

import io.github.helpdesk.service.OtpService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "otp")
public class OtpConfig {

    private OtpConfigProperties emailVerification;

    @Bean
    public OtpService emailVerificationOtpService(final RedisTemplate<String, String> redisTemplate,
                                                  final PasswordEncoder passwordEncoder) {
        return new OtpService(emailVerification, redisTemplate, passwordEncoder);
    }

    public record OtpConfigProperties(String cachePrefix, Duration ttl, Integer length) {
    }

    public OtpConfigProperties getEmailVerification() {
        return emailVerification;
    }

    public void setEmailVerification(OtpConfigProperties emailVerification) {
        this.emailVerification = emailVerification;
    }
}
