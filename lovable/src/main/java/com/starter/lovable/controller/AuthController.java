package com.starter.lovable.controller;

import com.starter.lovable.dto.auth.AuthResponse;
import com.starter.lovable.dto.auth.LoginRequest;
import com.starter.lovable.dto.auth.SignupRequest;
import com.starter.lovable.dto.auth.UserProfileResponse;
import com.starter.lovable.service.AuthService;
import com.starter.lovable.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthController {
    AuthService authService;
    UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request)
    {
        log.info("AuthController.signup called");
        log.debug("SignupRequest payload: {}", request);
        AuthResponse response = authService.signup(request);
        log.info("AuthController.signup completed for user={}", response.user().userName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request)
    {
        log.info("AuthController.login called");
        log.debug("LoginRequest payload: {}", request);
        AuthResponse response = authService.login(request);
        log.info("AuthController.login completed for user={}", request.userName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile()
    {
        log.info("AuthController.getProfile called");
        Long userId = 1L;
        UserProfileResponse response = userService.getProfile(userId);
        log.debug("Profile returned: {}", response);
        return ResponseEntity.ok(response);
    }
}
