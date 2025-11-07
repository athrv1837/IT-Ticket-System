package com.ticketsystem.ticketsystem.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.ticketsystem.ticketsystem.DTO.TicketCreateDTO;
import com.ticketsystem.ticketsystem.DTO.TicketUpdateInfo;
import com.ticketsystem.ticketsystem.entity.AuditLog;
import com.ticketsystem.ticketsystem.entity.Ticket;
import com.ticketsystem.ticketsystem.entity.User;
import com.ticketsystem.ticketsystem.repository.AuditLogRepository;
import com.ticketsystem.ticketsystem.repository.TicketRepository;
import com.ticketsystem.ticketsystem.service.TicketService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final AuditLogRepository auditLogRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, AuditLogRepository auditLogRepository) {
        this.ticketRepository = ticketRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public Ticket createTicket(TicketCreateDTO ticketCreateDTO, User creator) {
        Ticket ticket = new Ticket();
        ticket.setTitle(ticketCreateDTO.getTitle());
        ticket.setDescription(ticketCreateDTO.getDescription());
        ticket.setCategory(ticketCreateDTO.getCategory());
        ticket.setPriority(ticketCreateDTO.getPriority());
        ticket.setStatus(ticketCreateDTO.getStatus());
        ticket.setCreator(creator);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        ticket = ticketRepository.save(ticket);

        // Create audit log
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(ticket.getId());
        auditLog.setEntityType("TICKET");
        auditLog.setAction("CREATE");
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setUser(creator);
        auditLogRepository.save(auditLog);

        return ticket;
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + id));
    }

    @Override
    public Ticket updateTicket(Long id, TicketUpdateInfo updateInfo, User user) {
        Ticket ticket = getTicketById(id);

        // Check if user is the creator or has admin role
        if (!ticket.getCreator().equals(user) && !user.getRole().toString().equals("ADMIN")) {
            throw new AccessDeniedException("You don't have permission to update this ticket");
        }

        if (updateInfo.getTitle() != null) {
            ticket.setTitle(updateInfo.getTitle());
        }
        if (updateInfo.getDescription() != null) {
            ticket.setDescription(updateInfo.getDescription());
        }
        if (updateInfo.getCategory() != null) {
            ticket.setCategory(updateInfo.getCategory());
        }
        if (updateInfo.getPriority() != null) {
            ticket.setPriority(updateInfo.getPriority());
        }
        if (updateInfo.getStatus() != null) {
            ticket.setStatus(updateInfo.getStatus());
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);

        // Create audit log
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(ticket.getId());
        auditLog.setEntityType("TICKET");
        auditLog.setAction("UPDATE");
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setUser(user);
        auditLogRepository.save(auditLog);

        return ticket;
    }

    @Override
    public void deleteTicket(Long id, User user) {
        Ticket ticket = getTicketById(id);

        // Check if user is the creator or has admin role
        if (!ticket.getCreator().equals(user) && !user.getRole().toString().equals("ADMIN")) {
            throw new AccessDeniedException("You don't have permission to delete this ticket");
        }

        ticketRepository.delete(ticket);

        // Create audit log
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(id);
        auditLog.setEntityType("TICKET");
        auditLog.setAction("DELETE");
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setUser(user);
        auditLogRepository.save(auditLog);
    }

    @Override
    public List<Ticket> getTicketsByUser(User user) {
        return ticketRepository.findByCreator(user);
    }
}