package com.learning.section7.mcpclient.util;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.McpConnectionInfo;
import org.springframework.ai.mcp.McpToolFilter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MCPServerToolFilter implements McpToolFilter {

    @Override
    public boolean test(McpConnectionInfo mcpConnectionInfo, McpSchema.Tool tool) {
        assert mcpConnectionInfo.initializeResult() != null;
        String serverName = mcpConnectionInfo.initializeResult()
                .serverInfo()
                .name();

        String toolName = tool.name();

        log.info("Evaluating tool '{}' for MCP server '{}'", toolName, serverName);

        if(serverName.equalsIgnoreCase("github")){
            log.warn("Tool '{}' rejected because it belongs to blocker MCP server '{}'", toolName, serverName);
            return false;
        }

        if (toolName.contains("write_")){
            log.warn("Tool '{}' blocked because it belongs to the blocked list '{}'", toolName, serverName);
            return false;
        }

        log.info("Tool '{}' approved from MCP server '{}'", toolName, serverName);
        return true;
    }
}
