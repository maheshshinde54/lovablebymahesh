package com.starter.lovable.entity;

import com.starter.lovable.enums.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User
{
    Long Id;
    String email;
    String password;
    String fullName;
    //String avatarUrl;     we can use enum which will have fix set of avatars, their images where stored in the resource folder.
    UserRole role;
    Boolean isEmailVerified;
    Instant lastLogin;
    Instant createdAt;
    Instant updatedAt;
    Instant deletedAt;


}
