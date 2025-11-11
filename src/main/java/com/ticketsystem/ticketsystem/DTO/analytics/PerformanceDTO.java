package com.ticketsystem.ticketsystem.DTO.analytics;

import lombok.Data;

@Data
public class PerformanceDTO {
    private String username;
    private Long resolvedCount;
    private Double avgResolutionTimeHours;
}