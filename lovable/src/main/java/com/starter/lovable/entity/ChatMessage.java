package com.starter.lovable.entity;

import com.starter.lovable.enums.MessageRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false),
            @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    })
    ChatSession chatSession;

    @Enumerated(EnumType.STRING)
            @Column(nullable = false)
    MessageRole role;

@Column(columnDefinition = "text",nullable = false)
    String content;

    @Builder.Default // Ensures the builder picks up the 0 default
    @Column(name = "tokens_used", nullable = false, columnDefinition = "integer default 0")
    Integer tokensUsed = 0;
    @CreationTimestamp
    Instant createdAt;


}
