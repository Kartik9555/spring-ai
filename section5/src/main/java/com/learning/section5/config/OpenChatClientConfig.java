package com.learning.section5.config;

import com.learning.section5.advisors.TokenAuditUsageAdvisor;
import org.springframework.ai.chat.cache.semantic.SemanticCacheAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenChatClientConfig {

    @Bean("openChatClient")
    public ChatClient openChatClient(ChatClient.Builder chatClientBuilder, SemanticCacheAdvisor semanticCacheAdvisor) {
        return chatClientBuilder.defaultAdvisors(List.of(
                new SimpleLoggerAdvisor(),
                new TokenAuditUsageAdvisor(),
                semanticCacheAdvisor))
                .build();
    }
}
