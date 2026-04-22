package com.starter.lovable.service.impl;

import com.starter.lovable.dto.auth.UserProfileResponse;
import com.starter.lovable.respository.UserRepository;
import com.starter.lovable.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    UserRepository userRepository;

    @Override
    public UserProfileResponse getProfile(Long userId)
    {
        log.info("UserServiceImpl.getProfile called for userId={}", userId);
        log.debug("UserServiceImpl.getProfile currently returning null because implementation is pending");
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        log.info("UserServiceImpl.loadUserByUsername called for username={}", username);
        return userRepository.findByUserName(username)
                             .orElseThrow(() -> new UsernameNotFoundException("user with username " + username + " not found"));
    }
}
