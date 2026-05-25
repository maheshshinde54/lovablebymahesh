package com.starter.lovable.controller;

import com.starter.lovable.dto.chat.ChatRequest;
import com.starter.lovable.service.AiGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final AiGenerationService aiGenerationService;

    @PostMapping(value = "/streams", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(
            @RequestBody ChatRequest request){
        return aiGenerationService.streamResponse(request.message(), request.projectId())
                .map(data->ServerSentEvent.<String>builder().data(data).build());
    }

}
