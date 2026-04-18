package com.starter.lovable.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String name;
    @Column(unique = true)
    String stripePriceId;

    Integer maxTokensPerDay;
    Integer maxPreviews;
    Integer maxProjects;
    Boolean unlimitedAi;
    Boolean isActive;

}
