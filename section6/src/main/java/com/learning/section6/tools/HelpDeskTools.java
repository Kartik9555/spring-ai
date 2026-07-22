package com.learning.section6.tools;

import com.learning.section6.entity.HelpDeskTicket;
import com.learning.section6.model.TicketRequest;
import com.learning.section6.service.HelpDeskTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelpDeskTools {

    private final HelpDeskTicketService service;

    @Tool(name = "createTicket", description = "Create the Support ticket", returnDirect = true)
    String createTicket(@ToolParam(description = "Details to create a Support ticket")TicketRequest request, ToolContext context){
        String username = (String) context.getContext().get("username");
        log.info("Creating Support ticket for user: {} with details: {}", username, request);
        HelpDeskTicket ticket = service.createTicket(request, username);
        log.info("Ticket created successfully. Ticket ID: {}, Username: {}", ticket.getId(), username);
        return "Ticket #" + ticket.getId() + "created successfully for user " + ticket.getUsername();
    }

    @Tool(name = "getTicketStatus", description = "Fetch the status of the tickets based on a given username")
    List<HelpDeskTicket> getTicketStatus(ToolContext context) {
        String username = (String) context.getContext().get("username");
        log.info("Fetching tickets for user: {}", username);
        List<HelpDeskTicket> tickets = service.getTicketsByUsername(username);
        log.info("Found {} tickets for user: {}", tickets.size(), username);
//      throw new RuntimeException("Tickets for user " + username + " not found");
        return tickets;
    }
}
