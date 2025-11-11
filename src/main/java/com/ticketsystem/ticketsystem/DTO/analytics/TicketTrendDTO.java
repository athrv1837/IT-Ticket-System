package com.ticketsystem.ticketsystem.DTO.analytics;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TicketTrendDTO {
    private LocalDate date;
    private Long count;
}
