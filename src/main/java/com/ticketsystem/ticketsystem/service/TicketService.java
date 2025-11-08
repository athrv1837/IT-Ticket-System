package com.ticketsystem.ticketsystem.service;

import java.util.List;

import com.ticketsystem.ticketsystem.DTO.AuditLogDTO;
import com.ticketsystem.ticketsystem.DTO.TicketCreateDTO;
import com.ticketsystem.ticketsystem.DTO.TicketDTO;
import com.ticketsystem.ticketsystem.DTO.TicketUpdateInfo;
import com.ticketsystem.ticketsystem.entity.User;
import com.ticketsystem.ticketsystem.enums.TicketStatus;

public interface TicketService {
    TicketDTO createTicket(TicketCreateDTO ticketCreateDTO, User creator);
    List<TicketDTO> getAllTickets();
    TicketDTO getTicketById(Long id);
    TicketDTO updateTicket(Long id, TicketUpdateInfo updateInfo, User user);
    void deleteTicket(Long id, User user);
    List<TicketDTO> getTicketsByUser(User user);
    List<AuditLogDTO> getAuditLogs(Long ticketId);
    TicketDTO assignTicket(Long ticketId, Long userId, User currentUser);
    TicketDTO updateTicketStatus(Long id, TicketStatus status, User user);
}
