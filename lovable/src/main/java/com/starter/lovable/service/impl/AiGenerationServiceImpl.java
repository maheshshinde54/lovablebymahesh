package com.starter.lovable.service.impl;

import com.starter.lovable.llm.PromptUtils;
import com.starter.lovable.security.AuthUtil;
import com.starter.lovable.service.AiGenerationService;
import com.starter.lovable.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService
{

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;

    @PreAuthorize("@security.canEditProject(#projectId)")
    @Override
    public Flux<String> streamResponse(String userMessage, Long projectId)
    {
        Long userId = authUtil.getCurrentUserId();

        createChatSessionIfNotExist(projectId, userId);
        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
                                                  );
        StringBuilder fullResponseBuffer = new StringBuilder();

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userMessage)
                .advisors(advisorSpec -> {
                    advisorSpec.params(advisorParams);
                })
                .stream()
                .chatResponse()
                .doOnNext(response -> {
                    String content = response.getResult()
                            .getOutput()
                            .getText();

                    log.info("AAAA : {}",content);
                    fullResponseBuffer.append(content);
                    log.info("BBBB : {}",fullResponseBuffer);

                })
                .doOnComplete(() -> {
                    Schedulers.boundedElastic()
                            .schedule(() -> {
                                parseAndSaveFiles(fullResponseBuffer.toString(), projectId);
                            });
                    log.info("CCCC : {}",fullResponseBuffer);

                })
                .doOnError(error -> log.error("Error during streaming for the project id {}", projectId))
                .map(response -> response.getResult()
                        .getOutput()
                        .getText())
                ;
    }

    private void parseAndSaveFiles(String fullResponse, Long projectId)
    {
        log.info("dddd: {}", fullResponse);

        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);
        while (matcher.find()) {
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2)
                    .trim();
            log.info("full message from llm : {}", fullResponse);
            log.info("full filePath from llm : {}", filePath);
            log.info("full fileContent from llm : {}", fileContent);

            projectFileService.saveFile(projectId, filePath, fileContent);

        }

    }


    private void createChatSessionIfNotExist(Long projectId, Long userId)
    {

    }
}

