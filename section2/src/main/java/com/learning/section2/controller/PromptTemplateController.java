package com.learning.section2.controller;

import com.learning.section2.advisors.TokenAuditUsageAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PromptTemplateController {

    private final ChatClient chatClient;

    public PromptTemplateController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Value("classpath:/promptTemplates/userEmailPromptTemplate.st")
    Resource userEmailPromptTemplate;

    @GetMapping("/email")
    public String emailResponse(@RequestParam("customerName") String customerName, @RequestParam("customerMessage") String customerMessage) {
        return chatClient.prompt()
                //.advisors(new TokenAuditUsageAdvisor())
                .system("""
                        You are a professional customer service assistant which helps drafting email
                        responses to improve the productivity of the customer support team
                        """)
                .user(userPromptTemplateSpec -> userPromptTemplateSpec.text(userEmailPromptTemplate)
                        .param("customerName", customerName)
                        .param("customerMessage", customerMessage)
                )
                .call()
                .content();
    }
}
