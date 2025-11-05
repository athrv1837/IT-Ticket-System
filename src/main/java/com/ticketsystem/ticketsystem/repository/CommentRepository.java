package com.ticketsystem.ticketsystem.repository;

import java.util.List;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticketsystem.ticketsystem.entity.Comment;
import com.ticketsystem.ticketsystem.entity.Ticket;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{
    List<Comment> findByTicket(Ticket ticket);
}
