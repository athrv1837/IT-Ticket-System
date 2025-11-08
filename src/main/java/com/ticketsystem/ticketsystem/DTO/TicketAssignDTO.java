package com.ticketsystem.ticketsystem.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketAssignDTO {
    @NotNull
    private Long assignedToUserId;
}