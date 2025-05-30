package io.github.helpdesk.controller;

import io.github.helpdesk.dto.UserDto;
import io.github.helpdesk.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/profile")
@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getUser(Authentication authentication) {
        log.debug("User {} is fetching Profile", authentication.getName());
        return ResponseEntity.ok(userService.getUserDtoByUserName(authentication.getName()));
    }

    @PutMapping
    public ResponseEntity<?> fullUpdate(Authentication authentication, @RequestBody UserDto userDto) {
        log.debug("User {} is full updating Profile", authentication.getName());
        userService.fullUpdate(authentication.getName(), userDto);
        return ResponseEntity.noContent().build();
    }

}

