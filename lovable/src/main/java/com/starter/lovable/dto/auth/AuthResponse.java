package com.starter.lovable.dto.auth;

public record AuthResponse(String token,
                           UserProfileResponse user) {


}
