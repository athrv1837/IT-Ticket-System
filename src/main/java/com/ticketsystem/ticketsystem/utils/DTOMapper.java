package com.ticketsystem.ticketsystem.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ticketsystem.ticketsystem.DTO.AuditLogDTO;
import com.ticketsystem.ticketsystem.DTO.CommentDTO;
import com.ticketsystem.ticketsystem.DTO.TicketDTO;
import com.ticketsystem.ticketsystem.DTO.UserDTO;
import com.ticketsystem.ticketsystem.entity.AuditLog;
import com.ticketsystem.ticketsystem.entity.Comment;
import com.ticketsystem.ticketsystem.entity.Ticket;
import com.ticketsystem.ticketsystem.entity.User;

@Component
public class DTOMapper {
    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        return userDTO;
    }

    public List<UserDTO> toUserList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream().map(this::toUserDTO).collect(Collectors.toList());
    }

    public TicketDTO toTicketDTO(Ticket ticket){
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setId(ticket.getId());
        ticketDTO.setTitle(ticket.getTitle());
        ticketDTO.setDescription(ticket.getDescription());
        ticketDTO.setCategory(ticket.getCategory());
        ticketDTO.setStatus(ticket.getStatus());
        ticketDTO.setPriority(ticket.getPriority());
        ticketDTO.setCreatorId(ticket.getCreator().getId());
        ticketDTO.setCreatorUsername(ticket.getCreator().getUsername());
        ticketDTO.setAssignedToId(ticket.getAssignedTo().getId());
        ticketDTO.setAssignedToUsername(ticket.getAssignedTo().getUsername());
        ticketDTO.setCreatedAt(ticket.getCreatedAt());
        ticketDTO.setUpdatedAt(ticket.getUpdatedAt());
        return ticketDTO;
    }

    public List<TicketDTO> toTicketList(List<Ticket>tickets){
        if(tickets == null){
            return null;
        }

        return tickets.stream().map(this::toTicketDTO).collect(Collectors.toList());
    }

    public AuditLogDTO toAuditLogDTO(AuditLog auditLog){
        AuditLogDTO auditLogDTO = new AuditLogDTO();
        auditLogDTO.setId(auditLog.getId());
        auditLogDTO.setAction(auditLog.getAction());
        auditLogDTO.setChanges(auditLog.getChanges());
        auditLogDTO.setEntityId(auditLog.getEntityId());
        auditLogDTO.setEntityType(auditLog.getEntityType());
        auditLogDTO.setTimestamp(auditLog.getTimestamp());   
        auditLogDTO.setUserUsername(auditLog.getUser().getUsername());
        return auditLogDTO;
    }

    public List<AuditLogDTO> toAdAuditLogDTOList(List<AuditLog>AuditLogs){
        if(AuditLogs == null){
            return null;
        }

        return AuditLogs.stream().map(this::toAuditLogDTO).collect(Collectors.toList());
    }

    public CommentDTO toCommentDTO(Comment comment){
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        return commentDTO;
    }

    public List<CommentDTO> tCommentDTOList(List<Comment>comments){
        if(comments == null){
            return null;
        }

        return comments.stream().map(this::toCommentDTO).collect(Collectors.toList());
    }

}
