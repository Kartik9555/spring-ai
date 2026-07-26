package com.learning.section7.mcpclient.util;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpLogging;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelpDeskLogBridge {

    @McpLogging(clients = "demo")
    public void onServerLog(McpSchema.LoggingLevel level, String source, String message) {
        log.info("Received log from server - Level: {}, Source: {}, Message: {}", level, source, message);
    }

}
