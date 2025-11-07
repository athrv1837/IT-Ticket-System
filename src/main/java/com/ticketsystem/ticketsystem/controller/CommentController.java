package com.ticketsystem.ticketsystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ticketsystem.ticketsystem.DTO.CommentDTO;
import com.ticketsystem.ticketsystem.entity.Comment;
import com.ticketsystem.ticketsystem.entity.UserPrincipal;
import com.ticketsystem.ticketsystem.service.CommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class CommentController {

    @Autowired
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(
            @PathVariable Long ticketId,
            @Valid @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Comment comment = commentService.addComment(ticketId, commentDTO, userPrincipal.getUser());
        return ResponseEntity.ok(comment);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getTicketComments(@PathVariable Long ticketId) {
        return ResponseEntity.ok(commentService.getCommentsByTicket(ticketId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long ticketId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        commentService.deleteComment(ticketId, commentId, userPrincipal.getUser());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long ticketId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Comment comment = commentService.updateComment(ticketId, commentId, commentDTO, userPrincipal.getUser());
        return ResponseEntity.ok(comment);
    }
}