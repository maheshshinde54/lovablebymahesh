package com.starter.lovable.entity;

import com.starter.lovable.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;
    String email;
    String password;
    String name;
    @Column(name = "\"avatarUrl\"")
    String avatarUrl;     //we can use enum which will have fix set of avatars, their images where stored in the resource folder.

    @Enumerated(EnumType.STRING)
    UserRole role;
    Boolean isEmailVerified;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;
    Instant deletedAt;


}
