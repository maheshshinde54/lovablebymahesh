package com.starter.lovable.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public record JwtUserPrincipal(Long userId,
                               String userName,
                               List<GrantedAuthority> authorities) {
}
