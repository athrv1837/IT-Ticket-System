package com.ticketsystem.ticketsystem.repository;

import java.util.List;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticketsystem.ticketsystem.entity.Comments;
import com.ticketsystem.ticketsystem.entity.Ticket;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long>{
    List<Comments> findByTicket(Ticket ticket);
}
