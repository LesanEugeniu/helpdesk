package io.github.helpdesk.service;

import io.github.helpdesk.dto.request.AuthenticationRequestDto;
import io.github.helpdesk.dto.request.RegistrationRequestDto;
import io.github.helpdesk.exception.RestErrorResponseException;
import io.github.helpdesk.model.User;
import io.github.helpdesk.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static io.github.helpdesk.exception.ErrorType.RESOURCE_ALREADY_EXISTS;
import static io.github.helpdesk.exception.ProblemDetailBuilder.forStatusAndDetail;
import static org.springframework.http.HttpStatus.CONFLICT;

@Service
public class AuthenticationService {

    @Value("${session.max}")
    private int maxSession;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityContextRepository securityContextRepository;

    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    private final AuthenticationManager authManager;

    private final RedisIndexedSessionRepository redisIndexedSessionRepository;

    private final SessionRegistry sessionRegistry;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 SecurityContextRepository securityContextRepository,
                                 SecurityContextHolderStrategy securityContextHolderStrategy,
                                 AuthenticationManager authManager,
                                 RedisIndexedSessionRepository redisIndexedSessionRepository,
                                 SessionRegistry sessionRegistry) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityContextRepository = securityContextRepository;
        this.securityContextHolderStrategy = securityContextHolderStrategy;
        this.authManager = authManager;
        this.redisIndexedSessionRepository = redisIndexedSessionRepository;
        this.sessionRegistry = sessionRegistry;
    }

    @Transactional
    public void register(RegistrationRequestDto request) {
        final var errors = new HashMap<String, List<String>>();

        if (userRepository.existsByEmail(request.email()).isPresent()) {
            errors.put("email", List.of("Email is already taken"));
        }

        if (userRepository.existsByUserName(request.userName()).isPresent()) {
            errors.put("userName", List.of("UserName is already taken"));
        }

        if (!errors.isEmpty()) {
            throw new RestErrorResponseException(forStatusAndDetail(CONFLICT, "Request validation failed")
                    .withProperty("errors", errors)
                    .withErrorType(RESOURCE_ALREADY_EXISTS)
                    .build()
            );
        }

        userRepository.save(new User()
                .setEmail(request.email())
                .setUserName(request.userName())
                .setEmailVerified(false)
                .setPassword(passwordEncoder.encode(request.password())));
    }

    public void login(AuthenticationRequestDto authenticationRequest,
                      HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(
                authenticationRequest.userName(), authenticationRequest.password()));

        validateMaxSession(authentication);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);
    }

    /**
     * If it has been exceeded, the oldest valid session is removed / invalidated
     */
    private void validateMaxSession(Authentication authentication) {
        // If max session is negative means unlimited session
        if (maxSession <= 0) {
            return;
        }

        var principal = (UserDetails) authentication.getPrincipal();
        List<SessionInformation> sessions = this.sessionRegistry.getAllSessions(principal, false);

        if (sessions.size() >= maxSession) {
            sessions.stream()
                    .min(Comparator.comparing(SessionInformation::getLastRequest))
                    .ifPresent(sessionInfo -> this.redisIndexedSessionRepository.deleteById(sessionInfo.getSessionId()));
        }
    }

}
