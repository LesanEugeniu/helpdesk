package io.github.helpdesk.service;

import io.github.helpdesk.dto.request.EmailVerificationRequestDto;
import io.github.helpdesk.dto.request.UserNameRequestDto;
import io.github.helpdesk.exception.RestErrorResponseException;
import io.github.helpdesk.model.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static io.github.helpdesk.exception.ErrorType.EMAIL_ALREADY_VERIFIED;
import static io.github.helpdesk.exception.ErrorType.EMAIL_VERIFICATION_FAILED;
import static io.github.helpdesk.exception.ProblemDetailBuilder.forStatusAndDetail;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class EmailVerificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);

    private final UserService userService;

    private final OtpService otpService;

    private final JavaMailSender mailSender;


    public EmailVerificationService(UserService userService,
                                    OtpService otpService,
                                    JavaMailSender mailSender) {
        this.userService = userService;
        this.otpService = otpService;
        this.mailSender = mailSender;
    }

    @Async
    public void sendVerificationOtp(final String userName, final String email) {
        final var token = otpService.generateAndStoreOtp(userName);
        final var emailText = "Enter the following email verification code: " + token;

        final var message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification");
        message.setFrom("System");
        message.setText(emailText);

        mailSender.send(message);
    }

    public void resendVerificationOtp(UserNameRequestDto request) {
        final String userName = request.userName();

        User user = userService.getUserByUserName(userName);
        if (!user.isEmailVerified()) {
            sendVerificationOtp(userName, user.getEmail());
        } else {
            log.warn("Attempt to resend verification token for non existing or already validated email: [{}]", user.getEmail());
        }
    }

    @Transactional
    public User verifyEmailOtp(EmailVerificationRequestDto request) {
        final String userName = request.userName();
        final String otp = request.otp();

        User user;
        try {
            user = userService.getUserByUserName(userName);
        } catch (RestErrorResponseException e) {
            throw new RestErrorResponseException(forStatusAndDetail(BAD_REQUEST, "Invalid userName or token")
                    .withErrorType(EMAIL_VERIFICATION_FAILED)
                    .build());
        }

        if (!otpService.isOtpValid(userName, otp)) {
            throw new RestErrorResponseException(forStatusAndDetail(BAD_REQUEST, "Invalid email or token")
                    .withErrorType(EMAIL_VERIFICATION_FAILED)
                    .build()
            );
        }
        otpService.deleteOtp(userName);

        if (user.isEmailVerified()) {
            throw new RestErrorResponseException(forStatusAndDetail(BAD_REQUEST, "Email is already verified")
                    .withErrorType(EMAIL_ALREADY_VERIFIED)
                    .build()
            );
        }

        user.setEmailVerified(true);

        return user;
    }

}
