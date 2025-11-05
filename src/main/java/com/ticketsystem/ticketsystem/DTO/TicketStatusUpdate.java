package com.ticketsystem.ticketsystem.DTO;

import com.ticketsystem.ticketsystem.enums.TicketStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketStatusUpdate {
    @NotNull(message = "Status cannot be null")
    private TicketStatus status;
}
