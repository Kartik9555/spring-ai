package com.learning.section9.config;

import com.learning.section9.tools.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Configuration
public class HelpDeskChatClientConfig {

    @Value("classpath:/promptTemplates/helpDeskSystemPromptTemplate.st")
    Resource promptTemplate;

    @Bean("helpDeskChatClient")
    public ChatClient helpDeskChatClient(ChatClient.Builder clientBuilder, ChatMemory chatMemory, TimeTools timeTools) {
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return clientBuilder
                .defaultAdvisors(List.of(memoryAdvisor))
                .defaultTools(List.of(timeTools))
                .defaultSystem(promptTemplate)
                .build();
    }
//
//    @Bean
//    public ToolExecutionExceptionProcessor exceptionProcessor() {
//        return new DefaultToolExecutionExceptionProcessor(true);
//    }
}
