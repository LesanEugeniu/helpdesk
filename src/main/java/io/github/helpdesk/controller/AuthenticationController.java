package io.github.helpdesk.controller;

import io.github.helpdesk.dto.request.AuthenticationRequestDto;
import io.github.helpdesk.dto.request.ForgotPasswordEmailRequestDto;
import io.github.helpdesk.dto.request.RegistrationRequestDto;
import io.github.helpdesk.dto.request.ResetPasswordRequestDto;
import io.github.helpdesk.service.AuthenticationService;
import io.github.helpdesk.service.PasswordUpdateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final PasswordUpdateService passwordUpdateService;

    public AuthenticationController(AuthenticationService authenticationService,
                                    PasswordUpdateService passwordUpdateService) {
        this.authenticationService = authenticationService;
        this.passwordUpdateService = passwordUpdateService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegistrationRequestDto requestDto) {
        this.authenticationService.register(requestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody AuthenticationRequestDto authenticationRequestDto,
                                   HttpServletRequest request, HttpServletResponse response) {
        authenticationService.login(authenticationRequestDto, request, response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendForgotPasswordLink(ForgotPasswordEmailRequestDto request) {
        passwordUpdateService.sendForgotPasswordLink(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reset-password")
    public ResponseEntity<?> validateResetToken(@RequestBody ResetPasswordRequestDto request) {
        passwordUpdateService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}
