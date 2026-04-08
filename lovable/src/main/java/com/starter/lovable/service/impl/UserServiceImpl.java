package com.starter.lovable.service.impl;

import com.starter.lovable.dto.auth.UserProfileResponse;
import com.starter.lovable.respository.UserRepository;
import com.starter.lovable.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    UserRepository userRepository;
    @Override
    public UserProfileResponse getProfile(Long userId)
    {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        return userRepository.findByUserName(username)
                             .orElseThrow(() -> new UsernameNotFoundException("user with userId " + username + " not found"));
    }
}
