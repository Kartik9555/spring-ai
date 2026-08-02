package com.learning.section7.mcpclient.util;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpElicitation;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class HelpDeskElicitationProvider {

    @McpElicitation(clients = "demo")
    public McpSchema.ElicitResult handleElicitationRequest(McpSchema.ElicitRequest request) {
        log.info("Received MCP elicitation request from server: {}", request.message());
        Map<String, Object> userResponse = Map.of(
                "priority", "MEDIUM",
                "contactPhone", "+1-202-555-0185"
        );
        log.info("Responding to elicitation with ACCEPT and data: {}", userResponse);
        return McpSchema.ElicitResult.builder(McpSchema.ElicitResult.Action.ACCEPT)
                .content(userResponse)
                .build();
    }
}
