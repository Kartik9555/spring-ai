package com.learning.section5.controller;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ChatClient chatMemoryChatClientRagAdvisor;

    public RagController(@Qualifier("chatMemoryChatClient") ChatClient chatClient, VectorStore vectorStore,
                         @Qualifier("chatMemoryChatClientRagAdvisor") ChatClient chatMemoryChatClientRagAdvisor) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.chatMemoryChatClientRagAdvisor = chatMemoryChatClientRagAdvisor;
    }

    @Value("classpath:/promptTemplates/systemPromptRandomDataTemplate.st")
    Resource promptTemplate;

    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    Resource hrSystemTemplate;

    @GetMapping("/random/chat")
    public ResponseEntity<String> randomChat(@RequestHeader("username")  String username,
                                             @RequestParam("message") String message) {
        String similarContext = getSimilarContext(message);

        String answer = chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(promptTemplate)
                        .param("documents", similarContext))
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username))
                .user(message)
                .call()
                .content();

        return ResponseEntity.ok(answer);

    }

    @GetMapping("/document/chat")
    public ResponseEntity<String> documentChat(@RequestHeader("username")  String username,
                                             @RequestParam("message") String message) {
        String similarContext = getSimilarContext(message);

        String answer = chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(hrSystemTemplate)
                        .param("documents", similarContext))
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username))
                .user(message)
                .call()
                .content();

        return ResponseEntity.ok(answer);

    }

    private @NonNull String getSimilarContext(String message) {
        SearchRequest request = SearchRequest.builder()
                .query(message)
                .topK(3)
                .similarityThreshold(0.5)
                .build();

        List<Document> similarityDocs = vectorStore.similaritySearch(request);
        return similarityDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @GetMapping("/document/chat-rag-advisor")
    public ResponseEntity<String> documentChatWithAdvisor(@RequestHeader("username")  String username,
                                               @RequestParam("message") String message) {
        String answer = chatMemoryChatClientRagAdvisor.prompt()
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username))
                .user(message)
                .call()
                .content();

        return ResponseEntity.ok(answer);

    }
}
