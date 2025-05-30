package io.github.helpdesk.service;

import io.github.helpdesk.dto.request.ForgotPasswordEmailRequestDto;
import io.github.helpdesk.dto.request.ResetPasswordRequestDto;
import io.github.helpdesk.exception.ErrorType;
import io.github.helpdesk.exception.RestErrorResponseException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static io.github.helpdesk.exception.ProblemDetailBuilder.forStatusAndDetail;

@Service
public class PasswordUpdateService {

    private static final Logger log = LoggerFactory.getLogger(PasswordUpdateService.class);

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final TokenService tokenService;

    private final UserService userService;

    private final JavaMailSender mailSender;

    private final AuthenticationService authenticationService;

    public PasswordUpdateService(TokenService tokenService,
                                 UserService userService,
                                 JavaMailSender mailSender,
                                 AuthenticationService authenticationService) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.mailSender = mailSender;
        this.authenticationService = authenticationService;
    }

    @Async
    public void sendPasswordResetLink(final String email) {
        final var token = tokenService.generateAndStoreToken(email);
        final var emailText = String.format("%s: %s?=%s", "Password reset", frontendUrl, token);

        final var message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset");
        message.setFrom("System");
        message.setText(emailText);

        mailSender.send(message);
    }

    public void sendForgotPasswordLink(ForgotPasswordEmailRequestDto request) {
        sendPasswordResetLink(request.email());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDto request) {
        String email = tokenService.getEmail(request.token());

        if (email == null) {
            log.info("Token expired \"{}\"", request.token());
            throw new RestErrorResponseException(forStatusAndDetail(HttpStatus.NOT_FOUND, "Token expired")
                    .withErrorType(ErrorType.NOT_FOUND)
                    .build()
            );
        }

        String userName = userService.resetPassword(email, request.password());
        authenticationService.invalidateAllSessions(() -> userName);
        tokenService.deleteToken(request.token());
    }
}
