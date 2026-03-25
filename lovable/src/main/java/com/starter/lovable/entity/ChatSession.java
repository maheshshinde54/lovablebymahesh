package com.starter.lovable.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ChatSession {
    Project project;
    User user;
    String title;

    Instant createdAt;
    Instant updatedAt;

    //For soft delete
    Instant deletedAt;

}
