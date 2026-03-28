package com.starter.lovable.service.impl;

import com.starter.lovable.dto.auth.AuthResponse;
import com.starter.lovable.dto.auth.LoginRequest;
import com.starter.lovable.dto.auth.SignupRequest;
import com.starter.lovable.entity.User;
import com.starter.lovable.error.BadRequestException;
import com.starter.lovable.mapper.UserMapper;
import com.starter.lovable.respository.UserRepository;
import com.starter.lovable.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse signup(SignupRequest request)
    {
        userRepository.findByUserName(request.userName())
                      .ifPresent(user ->
                      {
                          throw new BadRequestException("User already exist with username " + request.userName());
                      });
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);
        return new AuthResponse("dummy", userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest request)
    {

        return null;
    }
}
