package com.learning.section7.mcpserverremote.service;

import com.learning.section7.mcpserverremote.entity.HelpDeskTicket;
import com.learning.section7.mcpserverremote.model.TicketRequest;
import com.learning.section7.mcpserverremote.repository.HelpDeskTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HelpDeskTicketService {

    private final HelpDeskTicketRepository helpDeskTicketRepository;

    public HelpDeskTicket createTicket(TicketRequest request, String priority, String contactPhone) {
        HelpDeskTicket ticket = HelpDeskTicket.builder()
                .username(request.username())
                .issue(request.issue())
                .status("OPEN")
                .priority(priority)
                .contactPhone(contactPhone)
                .createdAt(LocalDateTime.now())
                .eta(LocalDateTime.now().plusDays(7))
                .build();
        return helpDeskTicketRepository.save(ticket);
    }

    public List<HelpDeskTicket> getTicketsByUsername(String username) {
        return helpDeskTicketRepository.findByUsername(username);
    }
}
