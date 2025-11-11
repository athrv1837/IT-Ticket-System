// AnalyticsService.java
package com.ticketsystem.ticketsystem.service;

import com.ticketsystem.ticketsystem.DTO.analytics.*;

import java.util.List;
import java.util.Map;

public interface AnalyticsService {

    // Shared
    Map<String, Long> getOpenTicketsByPriority();
    Map<String, Long> getTicketsByStatus();
    Long getMyWorkload(Long userId);

    // Manager
    ComplianceDTO getSlaCompliance();
    Double getAverageResolutionTime();
    List<TicketTrendDTO> getTicketTrend(String period);
    List<CategoryCountDTO> getTopCategories(int limit);
    List<PerformanceDTO> getAgentPerformance();
}