package com.starter.lovable.service;

import com.starter.lovable.dto.auth.LoginRequest;
import com.starter.lovable.dto.auth.SignupRequest;

public interface AuthService
{
    AuthService signup(SignupRequest request);

    AuthService login(LoginRequest request);
}
