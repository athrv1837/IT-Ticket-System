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
import com.ticketsystem.ticketsystem.repository.AuditLogRepository;
import com.ticketsystem.ticketsystem.repository.CommentRepository;
import com.ticketsystem.ticketsystem.repository.TicketRepository;
import com.ticketsystem.ticketsystem.service.CommentService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final TicketRepository ticketRepository;
    @Autowired
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
    public Comment addComment(Long ticketId, CommentDTO commentDTO, User user) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + ticketId));

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setTicket(ticket);
        comment.setCreator(user);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        comment = commentRepository.save(comment);

        // Create audit log
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(comment.getId());
        auditLog.setEntityType("COMMENT");
        auditLog.setAction("CREATE");
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setUser(user);
        auditLogRepository.save(auditLog);

        return comment;
    }

    @Override
    public List<Comment> getCommentsByTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + ticketId));
        return commentRepository.findByTicketOrderByCreatedAtDesc(ticket);
    }

    @Override
    public void deleteComment(Long ticketId, Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getTicket().getId().equals(ticketId)) {
            throw new EntityNotFoundException("Comment not found in ticket: " + ticketId);
        }

        // Check if user is the creator or has admin role
        if (!comment.getCreator().equals(user) && !user.getRole().toString().equals("ADMIN")) {
            throw new AccessDeniedException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);

        // Create audit log
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(commentId);
        auditLog.setEntityType("COMMENT");
        auditLog.setAction("DELETE");
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setUser(user);
        auditLogRepository.save(auditLog);
    }

    @Override
    public Comment updateComment(Long ticketId, Long commentId, CommentDTO commentDTO, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getTicket().getId().equals(ticketId)) {
            throw new EntityNotFoundException("Comment not found in ticket: " + ticketId);
        }

        // Check if user is the creator or has admin role
        if (!comment.getCreator().equals(user) && !user.getRole().toString().equals("ADMIN")) {
            throw new AccessDeniedException("You don't have permission to update this comment");
        }

        comment.setContent(commentDTO.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        comment = commentRepository.save(comment);

        // Create audit log
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(comment.getId());
        auditLog.setEntityType("COMMENT");
        auditLog.setAction("UPDATE");
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setUser(user);
        auditLogRepository.save(auditLog);

        return comment;
    }
}