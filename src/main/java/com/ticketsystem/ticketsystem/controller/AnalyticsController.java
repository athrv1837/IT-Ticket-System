package com.ticketsystem.ticketsystem.controller;

import com.ticketsystem.ticketsystem.DTO.analytics.*;
import com.ticketsystem.ticketsystem.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    //IT_SUPPORT + MANAGER
    @GetMapping("/open-by-priority")
    public ResponseEntity<Map<String, Long>> getOpenTicketsByPriority() {
        return ResponseEntity.ok(analyticsService.getOpenTicketsByPriority());
    }

    @GetMapping("/by-status")
    public ResponseEntity<Map<String, Long>> getTicketsByStatus() {
        return ResponseEntity.ok(analyticsService.getTicketsByStatus());
    }

    @GetMapping("/my-workload")
    public ResponseEntity<Long> getMyWorkload(@RequestParam Long userId) {
        return ResponseEntity.ok(analyticsService.getMyWorkload(userId));
    }

    //MANAGER ONLY
    @GetMapping("/sla-compliance")
    public ResponseEntity<ComplianceDTO> getSlaCompliance() {
        return ResponseEntity.ok(analyticsService.getSlaCompliance());
    }

    @GetMapping("/avg-resolution-time")
    public ResponseEntity<Double> getAvgResolutionTime() {
        return ResponseEntity.ok(analyticsService.getAverageResolutionTime());
    }

    @GetMapping("/trend")
    public ResponseEntity<List<TicketTrendDTO>> getTrend(
            @RequestParam(defaultValue = "weekly") String period) {
        return ResponseEntity.ok(analyticsService.getTicketTrend(period));
    }

    @GetMapping("/top-categories")
    public ResponseEntity<List<CategoryCountDTO>> getTopCategories(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(analyticsService.getTopCategories(limit));
    }

    @GetMapping("/agent-performance")
    public ResponseEntity<List<PerformanceDTO>> getAgentPerformance() {
        return ResponseEntity.ok(analyticsService.getAgentPerformance());
    }
}