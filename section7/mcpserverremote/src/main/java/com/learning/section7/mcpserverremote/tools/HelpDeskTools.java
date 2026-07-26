package com.learning.section7.mcpserverremote.tools;

import com.learning.section7.mcpserverremote.entity.HelpDeskTicket;
import com.learning.section7.mcpserverremote.model.TicketRequest;
import com.learning.section7.mcpserverremote.service.HelpDeskTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelpDeskTools {

    private final HelpDeskTicketService service;

    @McpTool(name = "createTicket", description = "Create the Support ticket")
    String createTicket(@McpToolParam(description = "Details to create a Support ticket") TicketRequest request){
        log.info("Creating Support ticket for user: {} with details: {}", request.username(), request);
        HelpDeskTicket ticket = service.createTicket(request);
        log.info("Ticket created successfully. Ticket ID: {}, Username: {}", ticket.getId(), ticket.getUsername());
        return "Ticket #" + ticket.getId() + "created successfully for user " + ticket.getUsername();
    }

    @McpTool(name = "getTicketStatus", description = "Fetch the status of the tickets based on a given username")
    List<HelpDeskTicket> getTicketStatus(@McpToolParam(description = "Username to fetch the status of the Help Desk tickets") String username,
                                         McpSyncRequestContext context) throws InterruptedException {
        log.info("Fetching tickets for user: {}", username);
        context.info("Fetching tickets for user: " + username);
        List<HelpDeskTicket> tickets = service.getTicketsByUsername(username);
        log.info("Found {} tickets for user: {}", tickets.size(), username);
        context.info("Found " + tickets.size() + " tickets for user: " + username);
        for(int i = 0; i < 10; i++) {
            Thread.sleep(1000); // Sleep for 1 second
            int percent = (i*100)/10;
            context.progress(spec -> spec.progress(percent)
                    .message("Fetching tickets for user: " + username + " - " + percent + "% complete")
            );
        }
        return tickets;
    }
}
