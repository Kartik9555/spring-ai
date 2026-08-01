package com.learning.section7.mcpclient.controller;

import com.learning.section7.mcpclient.util.ToolUtil;
import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MCPClientController {

    private final ChatClient chatClient;
    private final List<McpSyncClient> mcpSyncClients;

    public MCPClientController(ChatClient.Builder chatClientBuilder,
//                               ToolCallbackProvider toolCallbackProvider
                               List<McpSyncClient> mcpSyncClients
    ) {
        this.chatClient = chatClientBuilder
//                .defaultTools(toolCallbackProvider)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.mcpSyncClients = mcpSyncClients;
    }

    @GetMapping("/chat")
    public String chat(@RequestHeader(value = "username", required = false) String username,
            @RequestParam("message") String message) {
        ToolCallback[] toolCallbacks = ToolUtil.selectToolsFor(mcpSyncClients, "helpdesk-mcp-server", null);
        return chatClient.prompt()
                .tools(toolCallbacks)
                .toolContext(Map.of("progressToken", UUID.randomUUID().toString()))
                .user(message + "My username is " + username)
                .call()
                .content();
    }

    @GetMapping("/summarize-tickets")
    public String summarizeTickets(@RequestHeader(value = "username") String username) {
        ToolCallback[] toolCallbacks = ToolUtil.selectToolsFor(mcpSyncClients, "helpdesk-mcp-server", null);
        return chatClient.prompt()
                .system("""
                        You orchestrate the 'summarizeTickets' tool. The tool already returns a complete,
                        customer-ready summary that was generated for this exact request. Return that tool
                        output to the user EXACTLY as-is: do no rewrite, reformat, shorten, expand,
                        rephrase, or add any commentary of your own. Your reply must be the verbatim tool
                        response and nothing else.
                        """)
                .user("Summarize all of my support tickets. My username is " + username)
                .tools(toolCallbacks)
                .toolContext(Map.of("progressToken", UUID.randomUUID().toString()))
                .call()
                .content();
    }
}
