package io.github.helpdesk.controller;

import io.github.helpdesk.dto.MapStructMapper;
import io.github.helpdesk.dto.request.AuthenticationRequestDto;
import io.github.helpdesk.dto.request.EmailVerificationRequestDto;
import io.github.helpdesk.dto.request.UserNameRequestDto;
import io.github.helpdesk.service.AuthenticationService;
import io.github.helpdesk.service.EmailVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    private final AuthenticationService authenticationService;

    private final MapStructMapper mapper;

    public EmailVerificationController(EmailVerificationService emailVerificationService,
                                       AuthenticationService authenticationService,
                                       MapStructMapper mapper) {
        this.emailVerificationService = emailVerificationService;
        this.authenticationService = authenticationService;
        this.mapper = mapper;
    }

    @PostMapping("/request-verification-email")
    public ResponseEntity<Void> resendVerificationOtp(@Valid @RequestBody UserNameRequestDto request) {
        emailVerificationService.resendVerificationOtp(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyOtp(@RequestBody @Valid EmailVerificationRequestDto verificationRequestDto,
                                       HttpServletRequest request, HttpServletResponse response) {
        emailVerificationService.verifyEmailOtp(verificationRequestDto);
        AuthenticationRequestDto authenticationRequest = mapper.mapTo(verificationRequestDto);
        authenticationService.login(authenticationRequest, request, response);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
