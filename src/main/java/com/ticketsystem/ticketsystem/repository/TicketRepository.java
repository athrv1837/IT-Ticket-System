package com.ticketsystem.ticketsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticketsystem.ticketsystem.entity.Ticket;
import com.ticketsystem.ticketsystem.entity.User;
import com.ticketsystem.ticketsystem.enums.TicketStatus;

@Repository
public interface TicketRepository extends JpaRepository<Ticket,Long>{
    List<Ticket> findByCreator(User creator);
    Optional<Ticket>findByIdAndStatus(Long id, TicketStatus status);
}
