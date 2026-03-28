package com.starter.lovable.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(@Email @NotBlank String userName,
                           @NotBlank @Size(min = 4, max = 15) String password) {

}
