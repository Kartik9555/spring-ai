package com.learning.section7.mcpclient.util;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpProgress;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelpDeskToolProgressListener {

    @McpProgress(clients = "demo")
    public void onProgress(McpSchema.ProgressNotification notification) {
        log.info("Progress update - {}% complete received for Request ID {}: Message: {}",
                notification.progress(),
                notification.progressToken(),
                notification.message());
    }
}
