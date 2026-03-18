package com.starter.lovable.entity;

import com.starter.lovable.enums.ProjectRole;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProjectMember
{
    ProjectMemberId id;
    Project  project;
    User user;
    ProjectRole projectRole;
    Instant invitedAt;
    Instant acceptedAt;

}
