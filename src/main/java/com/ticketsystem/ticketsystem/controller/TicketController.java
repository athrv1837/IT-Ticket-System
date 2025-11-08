package com.ticketsystem.ticketsystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ticketsystem.ticketsystem.DTO.AuditLogDTO;
import com.ticketsystem.ticketsystem.DTO.TicketAssignDTO;
import com.ticketsystem.ticketsystem.DTO.TicketCreateDTO;
import com.ticketsystem.ticketsystem.DTO.TicketDTO;
import com.ticketsystem.ticketsystem.DTO.TicketUpdateInfo;
import com.ticketsystem.ticketsystem.entity.UserPrincipal;
import com.ticketsystem.ticketsystem.enums.TicketStatus;
import com.ticketsystem.ticketsystem.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping("/create")
    public ResponseEntity<TicketDTO> createTicket(
            @Valid @RequestBody TicketCreateDTO ticketCreateDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        TicketDTO ticket = ticketService.createTicket(ticketCreateDTO, userPrincipal.getUser());
        return ResponseEntity.ok(ticket);
    }

    @GetMapping
    public ResponseEntity<List<TicketDTO>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TicketDTO> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketUpdateInfo updateInfo,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(ticketService.updateTicket(id, updateInfo, userPrincipal.getUser()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ticketService.deleteTicket(id, userPrincipal.getUser());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<List<TicketDTO>> getMyTickets(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(ticketService.getTicketsByUser(userPrincipal.getUser()));
    }

    @GetMapping("/{id}/audit")
    public ResponseEntity<List<AuditLogDTO>> getTicketAudit(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getAuditLogs(id));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<TicketDTO> assignTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketAssignDTO assignDTO,
            @AuthenticationPrincipal UserPrincipal principal) {

        TicketDTO ticket = ticketService.assignTicket(id, assignDTO.getAssignedToUserId(), principal.getUser());
        return ResponseEntity.ok(ticket);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketDTO> updateTicketStatus(
            @PathVariable Long id,
            @RequestParam("status") String status,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        if(status == null){
             return ResponseEntity.badRequest().build();
        }
        TicketStatus ticketStatus;
        try {
            ticketStatus = TicketStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        TicketDTO updatedTicket = ticketService.updateTicketStatus(id, ticketStatus, userPrincipal.getUser());
        return ResponseEntity.ok(updatedTicket);
    }
}