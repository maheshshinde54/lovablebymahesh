package com.starter.lovable.security;

import com.starter.lovable.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

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
        return Jwts.builder()
                   .subject(user.getUsername())
                   .claim("userId", user.getId())
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + 1000 * 60*5))
                   .signWith(getSecretKey())
                   .compact();
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
        return new JwtUserPrincipal(userId, username, new ArrayList<>());
    }

    public Long getCurrentUserId()
    {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal))
        {
            throw new AuthenticationCredentialsNotFoundException("no JWT Found");
        }
        return ((JwtUserPrincipal) authentication.getPrincipal()).userId();
    }

}
