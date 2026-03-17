package com.starter.lovable.controller;

import com.starter.lovable.dto.auth.LoginRequest;
import com.starter.lovable.dto.auth.SignupRequest;
import com.starter.lovable.dto.auth.UserProfileResponse;
import com.starter.lovable.service.AuthService;
import com.starter.lovable.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController
{
    private AuthService authService;
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthService> signup(SignupRequest request)
    {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthService> login(LoginRequest request)
    {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile()
    {
        Long userId = 1L;
        return ResponseEntity.ok(UserService.getProfile(userId));
    }
}
