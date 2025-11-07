package com.ticketsystem.ticketsystem.service;

import java.util.List;

import com.ticketsystem.ticketsystem.DTO.TicketCreateDTO;
import com.ticketsystem.ticketsystem.DTO.TicketDTO;
import com.ticketsystem.ticketsystem.DTO.TicketUpdateInfo;
import com.ticketsystem.ticketsystem.entity.Ticket;
import com.ticketsystem.ticketsystem.entity.User;

public interface TicketService {
    TicketDTO createTicket(TicketCreateDTO ticketCreateDTO, User creator);
    List<Ticket> getAllTickets();
    Ticket getTicketById(Long id);
    Ticket updateTicket(Long id, TicketUpdateInfo updateInfo, User user);
    void deleteTicket(Long id, User user);
    List<Ticket> getTicketsByUser(User user);
}
