package com.learning.multimodel.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MultimodelChatController {

    private final ChatClient openaiChatClient;
    private final ChatClient ollamaChatClient;

    public MultimodelChatController(@Qualifier("openaiChatClient") ChatClient openaiChatClient, @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.openaiChatClient = openaiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    @GetMapping("/openai/chat")
    public String openaiChat(@RequestParam("message") String message) {
        return openaiChatClient.prompt(message).call().content();
    }

    @GetMapping("/ollama/chat")
    public String ollamaChat(@RequestParam("message") String message) {
        return ollamaChatClient.prompt(message).call().content();
    }
}