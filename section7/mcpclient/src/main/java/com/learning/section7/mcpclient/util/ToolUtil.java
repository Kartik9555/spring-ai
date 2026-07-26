package com.learning.section7.mcpclient.util;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.mcp.SyncMcpToolCallback;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;

public class ToolUtil {

    public static ToolCallback[] selectToolsFor(List<McpSyncClient> mcpSyncClients, String serverName, String toolName) {
        return mcpSyncClients.stream()
                .flatMap(client -> client.listTools().tools().stream()
                        .filter(tool -> matches(client.getServerInfo().name(), serverName) && matches(tool.name(), toolName))
                        .map(tool -> (ToolCallback) SyncMcpToolCallback.builder()
                                .mcpClient(client)
                                .tool(tool)
                                .build()))
                .toArray(ToolCallback[]::new);
    }

    private static boolean matches(String actual, String hint) {
        return hint == null || hint.isBlank() || actual.toLowerCase().contains(hint.toLowerCase());
    }
}
