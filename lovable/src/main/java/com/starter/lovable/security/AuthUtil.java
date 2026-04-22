package com.starter.lovable.security;

import com.starter.lovable.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Component
public class AuthUtil {


    @Value("${jwt.secrete-key}")
    private String jwtSecreteKey;

    private SecretKey getSecretKey()
    {
        return Keys.hmacShaKeyFor(jwtSecreteKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user)
    {
        log.info("AuthUtil.generateAccessToken called for username={}", user.getUsername());
        String token = Jwts.builder()
                           .subject(user.getUsername())
                           .claim("userId", user.getId())
                           .issuedAt(new Date())
                           .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5))
                           .signWith(getSecretKey())
                           .compact();
        log.debug("AuthUtil.generateAccessToken generated token of length={}", token.length());
        return token;
    }

    public JwtUserPrincipal verifyAccessToken(String token)
    {
        Claims claims = Jwts.parser()
                            .verifyWith(getSecretKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

        Long userId = claims.get("userId", Long.class);
        String username = claims.getSubject();
        log.debug("AuthUtil.verifyAccessToken extracted userId={} username={}", userId, username);
        return new JwtUserPrincipal(userId, username, new ArrayList<>());
    }

    public Long getCurrentUserId()
    {
        log.debug("AuthUtil.getCurrentUserId called");
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal))
        {
            log.warn("AuthUtil.getCurrentUserId no valid JWT authentication found");
            throw new AuthenticationCredentialsNotFoundException("no JWT Found");
        }
        Long userId = ((JwtUserPrincipal) authentication.getPrincipal()).userId();
        log.debug("AuthUtil.getCurrentUserId returning userId={}", userId);
        return userId;
    }

}
