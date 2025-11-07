package com.ticketsystem.ticketsystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ticketsystem.ticketsystem.DTO.TicketCreateDTO;
import com.ticketsystem.ticketsystem.DTO.TicketDTO;
import com.ticketsystem.ticketsystem.DTO.TicketUpdateInfo;
import com.ticketsystem.ticketsystem.entity.Ticket;
import com.ticketsystem.ticketsystem.entity.UserPrincipal;
import com.ticketsystem.ticketsystem.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;


    @PostMapping
    public ResponseEntity<TicketDTO> createTicket(
            @Valid @RequestBody TicketCreateDTO ticketCreateDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        TicketDTO ticket = ticketService.createTicket(ticketCreateDTO, userPrincipal.getUser());
        return ResponseEntity.ok(ticket);
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(
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
    public ResponseEntity<List<Ticket>> getMyTickets(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(ticketService.getTicketsByUser(userPrincipal.getUser()));
    }
}