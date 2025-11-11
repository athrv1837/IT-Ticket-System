package com.ticketsystem.ticketsystem.DTO.analytics;

import lombok.Data;

@Data
public class ComplianceDTO {
    private Double complianceRate;
    private Long totalResolved;
    private Long slaCompliant;
}
