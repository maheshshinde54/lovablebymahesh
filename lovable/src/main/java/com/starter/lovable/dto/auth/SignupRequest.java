package com.starter.lovable.dto.auth;

public record SignupRequest(String email,
                            String name,
                            String password) {
}
