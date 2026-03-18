package com.starter.lovable.service;

import com.starter.lovable.dto.auth.UserProfileResponse;

public interface UserService {

    UserProfileResponse getProfile(Long userId);
}


