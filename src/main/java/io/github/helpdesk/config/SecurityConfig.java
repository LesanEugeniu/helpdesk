package io.github.helpdesk.config;

import io.github.helpdesk.config.security.HelpDeskAccessDeniedHandler;
import io.github.helpdesk.config.security.HelpDeskAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${session.max}")
    private int maxSession;

    private final RedisIndexedSessionRepository redisIndexedSessionRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationEntryPoint authEntryPoint;

    private final AccessDeniedHandler accessDeniedHandler;

    private final UserDetailsService detailsService;

    private final CustomLogoutHandler logoutHandler;

    private final String[] permitAllUrls = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/request-verification-email",
            "/api/v1/auth/verify-email",
    };

    public SecurityConfig(
            RedisIndexedSessionRepository redisIndexedSessionRepository,
            PasswordEncoder passwordEncoder,
            HelpDeskAuthenticationEntryPoint authEntryPoint,
            HelpDeskAccessDeniedHandler accessDeniedHandler,
            UserDetailsService detailsService, CustomLogoutHandler logoutHandler
    ) {
        this.redisIndexedSessionRepository = redisIndexedSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.authEntryPoint = authEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.detailsService = detailsService;
        this.logoutHandler = logoutHandler;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(this.passwordEncoder);
        provider.setUserDetailsService(this.detailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(permitAllUrls).permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(IF_REQUIRED)
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession)
                        .maximumSessions(maxSession)
                        .sessionRegistry(sessionRegistry())
                )
                .exceptionHandling((ex) -> ex
                        .authenticationEntryPoint(this.authEntryPoint)
                        .accessDeniedHandler(this.accessDeniedHandler)
                )
                .logout(out -> out
                        .logoutUrl("/api/v1/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) ->
                                SecurityContextHolder.clearContext()
                        )
                )
                .build();
    }

    @Bean
    public SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(this.redisIndexedSessionRepository);
    }


    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityContextHolderStrategy securityContextHolderStrategy() {
        return SecurityContextHolder.getContextHolderStrategy();
    }

}
