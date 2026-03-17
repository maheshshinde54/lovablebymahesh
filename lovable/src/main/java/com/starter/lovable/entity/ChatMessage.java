package com.starter.lovable.entity;

import com.starter.lovable.enums.MessageRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {
    Long id;
    ChatSession chatSession;
    MessageRole role;
    String toolCalls;
    Integer tokensUsed;
    Instant createdAt;


}
