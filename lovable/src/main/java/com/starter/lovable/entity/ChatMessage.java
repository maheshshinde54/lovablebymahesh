package com.starter.lovable.entity;

import com.starter.lovable.enums.MessageRole;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    Long id;
    ChatSession chatSession;
    MessageRole role;
    String toolCalls;
    Integer tokensUsed;
    Instant createdAt;


}
