package com.learning.section2.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.ai.openai.api.OpenAiApi.ChatModel.GPT_4_1_MINI;

@RestController
@RequestMapping("/api")
public class PromptStuffingController {

    private final ChatClient chatClient;

    public PromptStuffingController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Value("classpath:/promptTemplates/systemHRPromptTemplate.st")
    Resource systemHRPromptTemplate;

    @GetMapping("/prompt-stuffing")
    public String promptStuffing(@RequestParam("message") String message) {
        return chatClient.prompt()
                .options(OpenAiChatOptions.builder().model(GPT_4_1_MINI).build())
                //.advisors(new TokenAuditUsageAdvisor())
                .system(systemHRPromptTemplate)
                .user(message)
                .call()
                .content();
    }
}
