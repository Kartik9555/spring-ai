package com.learning.section7.mcpclient.util;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.annotation.McpSampling;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HelpDeskSamplingProvider {

    private final ChatModel chatModel;

    public HelpDeskSamplingProvider(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @McpSampling(clients = "demo")
    public McpSchema.CreateMessageResult handleSamplingRequest(McpSchema.CreateMessageRequest request) {
        log.info("Received MCP sampling request from server. System prompt: {}", request.systemPrompt());
        List<Message> messages = new ArrayList<>();
        if(request.systemPrompt() !=null && !request.systemPrompt().isBlank()) {
            messages.add(new SystemMessage(request.systemPrompt()));
        }
        String userMessage = request.messages().stream()
                .filter(message -> message.content() instanceof McpSchema.TextContent
                    && message.role().name().equalsIgnoreCase(McpSchema.Role.USER.name()))
                .map(message -> ((McpSchema.TextContent)message.content()).text())
                .collect(Collectors.joining("\n"));
        messages.add(new UserMessage(userMessage));

        ChatResponse response = chatModel.call(new Prompt(messages));
        if(response.getResult() == null) {
            throw new IllegalStateException("LLM returned no result for the MCP sampling request");
        }
        String generatedText = Objects.requireNonNullElse(response.getResult().getOutput().getText(), "");
        String model = response.getMetadata().getModel();
        log.info("LLM produced sampling response using model '{}' : {}", model, generatedText);
        return McpSchema.CreateMessageResult.builder(McpSchema.Role.ASSISTANT, generatedText, model).build();
    }
}
