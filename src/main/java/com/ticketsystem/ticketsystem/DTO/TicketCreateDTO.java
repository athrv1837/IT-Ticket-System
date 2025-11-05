package com.ticketsystem.ticketsystem.DTO;

import com.ticketsystem.ticketsystem.enums.TicketCategory;
import com.ticketsystem.ticketsystem.enums.TicketPriority;
import com.ticketsystem.ticketsystem.enums.TicketStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketCreateDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull
    private TicketCategory category;

    @NotNull
    private TicketPriority priority;

    private TicketStatus status = TicketStatus.NEW;
}
