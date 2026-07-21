package com.learning.section6.config;

import com.learning.section6.advisors.TokenAuditUsageAdvisor;
import com.learning.section6.tools.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TimeChatClientConfig {

    @Bean("timeChatClient")
    public ChatClient timeChatClient(ChatClient.Builder clientBuilder, ChatMemory chatMemory, TimeTools timeTools) {
        Advisor loggingAdvisor = new SimpleLoggerAdvisor();
        Advisor tokenUsageAdvisor = new TokenAuditUsageAdvisor();
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return clientBuilder
                .defaultAdvisors(List.of(loggingAdvisor, memoryAdvisor, tokenUsageAdvisor))
                .defaultTools(List.of(timeTools))
                .build();
    }
}
