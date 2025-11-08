package com.ticketsystem.ticketsystem.service;

import java.util.List;

import com.ticketsystem.ticketsystem.DTO.CommentDTO;
import com.ticketsystem.ticketsystem.entity.Comment;
import com.ticketsystem.ticketsystem.entity.User;

public interface CommentService {
    CommentDTO addComment(Long ticketId, CommentDTO commentDTO, User user);
    List<CommentDTO> getCommentsByTicket(Long ticketId);
    void deleteComment(Long ticketId, Long commentId, User user);
    CommentDTO updateComment(Long ticketId, Long commentId, CommentDTO commentDTO, User user);
}
