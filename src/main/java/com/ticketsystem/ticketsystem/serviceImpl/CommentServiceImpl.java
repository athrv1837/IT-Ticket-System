package com.ticketsystem.ticketsystem.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.ticketsystem.ticketsystem.DTO.CommentDTO;
import com.ticketsystem.ticketsystem.entity.AuditLog;
import com.ticketsystem.ticketsystem.entity.Comment;
import com.ticketsystem.ticketsystem.entity.Ticket;
import com.ticketsystem.ticketsystem.entity.User;
import com.ticketsystem.ticketsystem.enums.Role;
import com.ticketsystem.ticketsystem.repository.AuditLogRepository;
import com.ticketsystem.ticketsystem.repository.CommentRepository;
import com.ticketsystem.ticketsystem.repository.TicketRepository;
import com.ticketsystem.ticketsystem.service.CommentService;
import com.ticketsystem.ticketsystem.utils.DTOMapper;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private DTOMapper dtoMapper;

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final AuditLogRepository auditLogRepository;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            TicketRepository ticketRepository,
            AuditLogRepository auditLogRepository) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public CommentDTO addComment(Long ticketId, CommentDTO commentDTO, User user) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + ticketId));

        boolean isCreator = ticket.getCreator().equals(user);
        boolean isPrivileged = user.getRole() == Role.IT_SUPPORT || user.getRole() == Role.MANAGER;

        if (!isCreator && !isPrivileged) {
            throw new AccessDeniedException("You don’t have permission to comment on this ticket");
        }

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setTicket(ticket);
        comment.setCreator(user);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        comment = commentRepository.save(comment);

        //Audit log
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(comment.getId());
        auditLog.setEntityType("COMMENT");
        auditLog.setAction("CREATE");
        auditLog.setChanges("Comment added by " + user.getUsername());
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setUser(user);
        auditLogRepository.save(auditLog);

        return dtoMapper.toCommentDTO(comment);
    }

    @Override
    public List<CommentDTO> getCommentsByTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + ticketId));
        return dtoMapper.tCommentDTOList(commentRepository.findByTicketOrderByCreatedAtDesc(ticket));
    }

    @Override
    public void deleteComment(Long ticketId, Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getTicket().getId().equals(ticketId)) {
            throw new EntityNotFoundException("Comment not found in ticket: " + ticketId);
        }

        boolean isCreator = comment.getCreator().equals(user);
        boolean isPrivileged = user.getRole() == Role.IT_SUPPORT || user.getRole() == Role.MANAGER;

        if (!isCreator && !isPrivileged) {
            throw new AccessDeniedException("You don’t have permission to delete this comment");
        }

        commentRepository.delete(comment);

        //Audit log
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(commentId);
        auditLog.setEntityType("COMMENT");
        auditLog.setAction("DELETE");
        auditLog.setChanges("Comment deleted by " + user.getUsername());
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setUser(user);
        auditLogRepository.save(auditLog);
    }

    @Override
    public CommentDTO updateComment(Long ticketId, Long commentId, CommentDTO commentDTO, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getTicket().getId().equals(ticketId)) {
            throw new EntityNotFoundException("Comment not found in ticket: " + ticketId);
        }

        boolean isCreator = comment.getCreator().equals(user);
        boolean isPrivileged = user.getRole() == Role.IT_SUPPORT || user.getRole() == Role.MANAGER;

        if (!isCreator && !isPrivileged) {
            throw new AccessDeniedException("You don’t have permission to update this comment");
        }

        String oldContent = comment.getContent();
        comment.setContent(commentDTO.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        comment = commentRepository.save(comment);

        //Audit log
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(comment.getId());
        auditLog.setEntityType("COMMENT");
        auditLog.setAction("UPDATE");
        auditLog.setChanges(String.format("Comment updated by %s: \"%s\" → \"%s\"",
                user.getUsername(), oldContent, comment.getContent()));
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setUser(user);
        auditLogRepository.save(auditLog);

        return dtoMapper.toCommentDTO(comment);
    }
}
