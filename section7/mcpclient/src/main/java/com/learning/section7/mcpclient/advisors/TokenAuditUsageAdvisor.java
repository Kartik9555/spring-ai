package com.learning.section7.mcpclient.advisors;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

@Component
public class TokenAuditUsageAdvisor implements CallAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(TokenAuditUsageAdvisor.class);

    @Override
    public @NonNull ChatClientResponse adviseCall(@NonNull ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        ChatResponse chatResponse = chatClientResponse.chatResponse();
        if (chatResponse != null) {
            Usage usage = chatResponse.getMetadata().getUsage();
            logger.info("Token usage details : {}", usage);
        }
        return chatClientResponse;
    }

    @Override
    public @NonNull String getName() {
        return "TokenAuditUsageAdvisor";
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
