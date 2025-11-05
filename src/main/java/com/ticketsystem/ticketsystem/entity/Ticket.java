package com.ticketsystem.ticketsystem.entity;

import java.time.LocalDateTime;
import java.util.List;


import com.ticketsystem.ticketsystem.enums.TicketCategory;
import com.ticketsystem.ticketsystem.enums.TicketPriority;
import com.ticketsystem.ticketsystem.enums.TicketStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.NEW;
    
    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "entityId", cascade = CascadeType.ALL)
    private List<AuditLog> auditLogs;

}
