package com.learning.section7.mcpserverremote.tools;

import com.learning.section7.mcpserverremote.entity.HelpDeskTicket;
import com.learning.section7.mcpserverremote.model.TicketContactInfo;
import com.learning.section7.mcpserverremote.model.TicketRequest;
import com.learning.section7.mcpserverremote.service.HelpDeskTicketService;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.ai.mcp.annotation.context.StructuredElicitResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelpDeskTools {

    private final HelpDeskTicketService service;
    private static final String DEFAULT_PRIORITY = "MEDIUM";
    private static final String NO_PHONE_PROVIDED = "N/A";

    @McpTool(name = "createTicket", description = "Create the Support ticket")
    String createTicket(@McpToolParam(description = "Details to create a Support ticket") TicketRequest request, McpSyncRequestContext context){
        log.info("Creating Support ticket for user: {} with details: {}", request.username(), request);
        String priority = DEFAULT_PRIORITY;
        String contactPhone = NO_PHONE_PROVIDED;
        if(context.elicitEnabled()) {
            context.info("Asking for a few extra details before opening this ticket...");
            log.info("Requesting additional ticket details from the MCP client via elicitation...");
            StructuredElicitResult<TicketContactInfo> result = context.elicit(
              spec -> spec.message("""
                        Before we open your support ticket, please choose a priority(LOW, MEDIUM, HIGH OR URGENT) and share a 
                        contact phone number so our team can reach you.
                      """),
                    TicketContactInfo.class
            );
            log.info("Elicitation finished with action: {}", result.action());
            if(result.action() == McpSchema.ElicitResult.Action.ACCEPT && result.structuredContent() != null) {
                TicketContactInfo info = result.structuredContent();
                if(info.priority() != null && !info.priority().isBlank()) {
                    priority = info.priority();
                }
                if(info.contactPhone() != null && !info.contactPhone().isBlank()) {
                    contactPhone = info.contactPhone();
                }
                context.info("Thanks! Using priority '" + priority + "' and contact phone '" + contactPhone + "'");
            } else {
                context.info("No extra details provided. Opening the ticket with default priority '" + DEFAULT_PRIORITY + "'.");
            }
        } else {
            log.warn("Connected MCP client does not support elicitation. Using default ticket details");
        }
        HelpDeskTicket ticket = service.createTicket(request, priority, contactPhone);
        log.info("Ticket created successfully. Ticket ID: {}, Username: {}", ticket.getId(), ticket.getUsername());
        return "Ticket #" + ticket.getId() + "created successfully for user " + ticket.getUsername() + " with priority " + ticket.getPriority() + " (contact phone: " + ticket.getContactPhone() + ").";
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

    @McpTool(name = "summarizeTickets", description = "Generate a friendly, natural language summary of all the support tickets that belong to a given username")
    String summarizeTickets(String username, McpSyncRequestContext context) throws InterruptedException {
        log.info("Generating ticket summary for user: {}", username);
        List<HelpDeskTicket> tickets = service.getTicketsByUsername(username);
        if(tickets.isEmpty()) {
            return "No support tickets were found for user " + username + ".";
        }
        if(!context.sampleEnabled()) {
            log.warn("Connected MCP client does not support sampling. Returning raw ticket data instead.");
            return tickets.toString();
        }
        String ticketData = tickets.stream()
                .map(ticket -> "Ticket #" + ticket.getId() + " | Issue: " + ticket.getIssue()
                + " | Status: " + ticket.getStatus() + " | ETA: " + ticket.getEta())
                .collect(Collectors.joining("\n"));

        String systemPrompt = """
                You are a friendly help desk assistant. Using only the ticket data provided by the user, write a short, warm summary for the customer about the staus of their
                support tickets. Mention how many tickets they have in total, group them by status (OPEN, IN_PROGRESS, CLOSED) and reassure them about the ones that are still
                being worked on. Keep it under 120 words and do not invent any information that is not present in the ticket data.
                """;

        log.info("Requesting LLM completion from the MCP client via sampling...");
        context.info("Asking your AI assistant to summarize " + tickets.size() + " tickets for " + username);
        McpSchema.CreateMessageResult result = context.sample(spec -> spec.systemPrompt(systemPrompt)
                .message("Here are the support tickets for " + username + ":\n" + ticketData));

        String summary = ((McpSchema.TextContent) result.content()).text();
        log.info("Sampling response received. Model used by the client: {}", result.model());
        return summary;
    }
}
