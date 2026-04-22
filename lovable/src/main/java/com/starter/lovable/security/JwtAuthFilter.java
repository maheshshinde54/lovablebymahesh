package com.starter.lovable.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final AuthUtil authUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        try
        {
            log.info("JwtAuthFilter.doFilterInternal incoming request {}", request.getRequestURI());

            final String requestHearToken = request.getHeader("Authorization");
            if (requestHearToken == null || !requestHearToken.startsWith("Bearer "))
            {
                log.debug("JwtAuthFilter.doFilterInternal missing or invalid Authorization header");
                filterChain.doFilter(request, response);
                return;
            }

            String jwtToken = requestHearToken.split("Bearer ")[1];

            JwtUserPrincipal user = authUtil.verifyAccessToken(jwtToken);
            if (user != null && SecurityContextHolder.getContext()
                                                     .getAuthentication() == null)
            {
                log.debug("JwtAuthFilter.doFilterInternal authenticated userId={}", user.userId());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.authorities());

                SecurityContextHolder.getContext()
                                     .setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        }
        catch (Exception e)
        {
            log.warn("JwtAuthFilter.doFilterInternal caught exception: {}", e.getMessage());
            handlerExceptionResolver.resolveException(request,response,null,e);
        }

    }
}
