package com.ticketsystem.ticketsystem.DTO;

import com.ticketsystem.ticketsystem.enums.TicketCategory;
import com.ticketsystem.ticketsystem.enums.TicketPriority;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TicketUpdateInfo {
    @Size(min = 3, max = 100, message = "Title must be 3–100 chars")
    private String title;

    @Size(max = 2000, message = "Description too long")
    private String description;

    private TicketCategory category;

    private TicketPriority priority;

}
