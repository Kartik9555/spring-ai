package com.learning.section2.config;

import com.learning.section2.advisors.TokenAuditUsageAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder clientBuilder) {
        return clientBuilder
                .defaultOptions(ChatOptions.builder()
                        .model("gpt-4o-mini")
                        .temperature(0.8))
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), new TokenAuditUsageAdvisor()))
                .defaultSystem("""
                        You are an internal HR assistant. You assist employees with queries related to HR policies
                        only — such as leave entitlements, working hours, benefits, and code of conduct.
                        """)
                .defaultUser("How can you help me ?")
                .build();
    }
}
