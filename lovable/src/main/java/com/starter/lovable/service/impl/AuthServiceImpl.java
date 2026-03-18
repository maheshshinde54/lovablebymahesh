package com.starter.lovable.service.impl;

import com.starter.lovable.dto.auth.AuthResponse;
import com.starter.lovable.dto.auth.LoginRequest;
import com.starter.lovable.dto.auth.SignupRequest;
import com.starter.lovable.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponse signup(SignupRequest request)
    {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request)
    {
        return null;
    }
}
