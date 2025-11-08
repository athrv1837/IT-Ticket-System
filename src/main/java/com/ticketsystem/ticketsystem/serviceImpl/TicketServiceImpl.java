package com.ticketsystem.ticketsystem.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.ticketsystem.ticketsystem.DTO.AuditLogDTO;
import com.ticketsystem.ticketsystem.DTO.TicketCreateDTO;
import com.ticketsystem.ticketsystem.DTO.TicketDTO;
import com.ticketsystem.ticketsystem.DTO.TicketUpdateInfo;
import com.ticketsystem.ticketsystem.entity.AuditLog;
import com.ticketsystem.ticketsystem.entity.Ticket;
import com.ticketsystem.ticketsystem.entity.User;
import com.ticketsystem.ticketsystem.enums.Role;
import com.ticketsystem.ticketsystem.enums.TicketStatus;
import com.ticketsystem.ticketsystem.repository.AuditLogRepository;
import com.ticketsystem.ticketsystem.repository.TicketRepository;
import com.ticketsystem.ticketsystem.repository.UserRepository;
import com.ticketsystem.ticketsystem.service.TicketService;
import com.ticketsystem.ticketsystem.utils.DTOMapper;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private DTOMapper dtoMapper;

    @Autowired
    private UserRepository userRepository;

    private final TicketRepository ticketRepository;
    private final AuditLogRepository auditLogRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, AuditLogRepository auditLogRepository) {
        this.ticketRepository = ticketRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public TicketDTO createTicket(TicketCreateDTO ticketCreateDTO, User creator) {
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
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(ticket.getId());
        auditLog.setEntityType("TICKET");
        auditLog.setAction("CREATE");
        auditLog.setChanges(String.format("Ticket #%d created by %s", ticket.getId(), creator.getUsername()));
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setUser(creator);
        auditLogRepository.save(auditLog);

        return dtoMapper.toTicketDTO(ticket);
    }

    @Override
    public List<TicketDTO> getAllTickets() {
        List<Ticket> list = ticketRepository.findAll();
        return dtoMapper.toTicketList(list);
    }

    @Override
    public TicketDTO getTicketById(Long id) {
        if (id == null)
            return null;

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + id));
        return dtoMapper.toTicketDTO(ticket);
    }

    @Override
    public TicketDTO updateTicket(Long id, TicketUpdateInfo updateInfo, User user) {
        if (id == null)
            return null;

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + id));

        if (!ticket.getCreator().equals(user) && user.getRole() != Role.IT_SUPPORT) {
            throw new AccessDeniedException("You don't have permission to update this ticket");
        }

        if (updateInfo.getTitle() != null)
            ticket.setTitle(updateInfo.getTitle());
        if (updateInfo.getDescription() != null)
            ticket.setDescription(updateInfo.getDescription());
        if (updateInfo.getCategory() != null)
            ticket.setCategory(updateInfo.getCategory());
        if (updateInfo.getPriority() != null)
            ticket.setPriority(updateInfo.getPriority());
        if (updateInfo.getStatus() != null)
            ticket.setStatus(updateInfo.getStatus());

        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);

        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(ticket.getId());
        auditLog.setEntityType("TICKET");
        auditLog.setAction("UPDATE");
        auditLog.setChanges(String.format("Ticket #%d updated by %s", ticket.getId(), user.getUsername()));
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setUser(user);
        auditLogRepository.save(auditLog);

        return dtoMapper.toTicketDTO(ticket);
    }

    @Override
    public void deleteTicket(Long id, User user) {
        if (id == null)
            return;

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + id));

        if (!ticket.getCreator().equals(user) && user.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("You don't have permission to delete this ticket");
        }

        ticketRepository.delete(ticket);

        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(id);
        auditLog.setEntityType("TICKET");
        auditLog.setAction("DELETE");
        auditLog.setChanges(String.format("Ticket #%d deleted by %s", id, user.getUsername()));
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setUser(user);
        auditLogRepository.save(auditLog);
    }

    @Override
    public List<TicketDTO> getTicketsByUser(User user) {
        List<Ticket> tickets = ticketRepository.findByCreator(user);
        return dtoMapper.toTicketList(tickets);
    }

    @Override
    public List<AuditLogDTO> getAuditLogs(Long ticketId) {
        List<AuditLog> list = auditLogRepository.findByEntityTypeAndEntityId("TICKET", ticketId);
        return dtoMapper.toAdAuditLogDTOList(list);
    }

    @Override
    public TicketDTO assignTicket(Long ticketId, Long userId, User currentUser) {
        if (ticketId == null || userId == null)
            return null;

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
        User assignee = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Only IT_SUPPORT or MANAGER can assign
        if (!(currentUser.getRole() == Role.IT_SUPPORT || currentUser.getRole() == Role.MANAGER)) {
            throw new AccessDeniedException("Only IT_SUPPORT or MANAGER can assign tickets");
        }

        // Assignee must be IT_SUPPORT
        if (assignee.getRole() != Role.IT_SUPPORT) {
            throw new AccessDeniedException("Cannot assign ticket to non-IT_SUPPORT user");
        }

        if (ticket.getAssignedTo() != null && ticket.getAssignedTo().equals(assignee)) {
            return dtoMapper.toTicketDTO(ticket);
        }

        // Set ticket status based on current state
        // if (ticket.getStatus() == TicketStatus.NEW) {
        // ticket.setStatus(TicketStatus.ASSIGNED);
        // } else {
        // ticket.setStatus(TicketStatus.IN_PROGRESS);
        // }

        //Update assignment and status
        if (ticket.getStatus() == TicketStatus.NEW) {
            ticket.setStatus(TicketStatus.ASSIGNED);
        } else if (ticket.getStatus() == TicketStatus.ASSIGNED && !assignee.equals(ticket.getAssignedTo())) {
            ticket.setStatus(TicketStatus.ASSIGNED);
        } else {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }

        ticket.setAssignedTo(assignee);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);

        AuditLog audit = new AuditLog();
        audit.setEntityType("TICKET");
        audit.setEntityId(ticketId);
        audit.setAction("ASSIGN");
        audit.setChanges(String.format("Ticket #%d assigned to %s by %s",
                ticketId, assignee.getUsername(), currentUser.getUsername()));
        audit.setUser(currentUser);
        audit.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(audit);

        return dtoMapper.toTicketDTO(ticket);
    }

    @Override
    public TicketDTO updateTicketStatus(Long id, TicketStatus newStatus, User user) {
        if (id == null)
            return null;

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + id));

        TicketStatus oldStatus = ticket.getStatus();

        boolean isAssigned = ticket.getAssignedTo() != null && ticket.getAssignedTo().equals(user);
        boolean isITSupport = user.getRole() == Role.IT_SUPPORT;
        boolean isManager = user.getRole() == Role.MANAGER;

        if (!isAssigned && !isITSupport && !isManager) {
            throw new AccessDeniedException("You are not authorized to update ticket status");
        }

        if (newStatus == TicketStatus.CLOSED && !isManager) {
            throw new AccessDeniedException("Only MANAGER can close a ticket");
        }

        // if (ticket.getStatus() == TicketStatus.CLOSED && newStatus !=
        // TicketStatus.CLOSED) {
        // throw new IllegalStateException("Cannot reopen a closed ticket");
        // }

        if (oldStatus == TicketStatus.CLOSED && newStatus != TicketStatus.CLOSED) {
            throw new IllegalStateException("Cannot reopen a closed ticket");
        }

        if (!isValidTransition(ticket.getStatus(), newStatus)) {
            throw new IllegalStateException("Invalid status transition: " + ticket.getStatus() + " → " + newStatus);
        }

        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);

        AuditLog audit = new AuditLog();
        audit.setEntityType("TICKET");
        audit.setEntityId(id);
        audit.setAction("STATUS_UPDATE");
        audit.setChanges(String.format(
                "Status changed from %s to %s by %s",
                oldStatus, newStatus, user.getUsername()));
        audit.setUser(user);
        audit.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(audit);

        return dtoMapper.toTicketDTO(ticket);
    }

    private boolean isValidTransition(TicketStatus from, TicketStatus to) {
        if (from == null || to == null)
            return false;

        return switch (from) {
            case NEW -> to == TicketStatus.ASSIGNED || to == TicketStatus.IN_PROGRESS || to == TicketStatus.CLOSED;
            case ASSIGNED -> to == TicketStatus.IN_PROGRESS || to == TicketStatus.CLOSED;
            case IN_PROGRESS -> to == TicketStatus.RESOLVED || to == TicketStatus.CLOSED;
            case RESOLVED -> to == TicketStatus.CLOSED;
            case CLOSED -> false;
        };
    }
}
