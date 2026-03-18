package com.starter.lovable.service.impl;

import com.starter.lovable.dto.auth.UserProfileResponse;
import com.starter.lovable.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserProfileResponse getProfile(Long userId)
    {
        return null;
    }
}
