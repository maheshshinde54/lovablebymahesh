package com.starter.lovable.mapper;

import com.starter.lovable.dto.auth.SignupRequest;
import com.starter.lovable.dto.auth.UserProfileResponse;
import com.starter.lovable.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignupRequest signupRequest);
    UserProfileResponse toUserProfileResponse(User user);
}
