package com.learning.section9.config;

import com.learning.section9.tools.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TimeChatClientConfig {

    @Bean("timeChatClient")
    public ChatClient timeChatClient(ChatClient.Builder clientBuilder, ChatMemory chatMemory, TimeTools timeTools) {
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return clientBuilder
                .defaultAdvisors(List.of(memoryAdvisor))
                .defaultTools(List.of(timeTools))
                .build();
    }
}
