package com.ticketsystem.ticketsystem.serviceImpl;

import com.ticketsystem.ticketsystem.DTO.analytics.*;
import com.ticketsystem.ticketsystem.entity.Ticket;
import com.ticketsystem.ticketsystem.entity.User;
import com.ticketsystem.ticketsystem.enums.Role;
import com.ticketsystem.ticketsystem.enums.TicketStatus;
import com.ticketsystem.ticketsystem.repository.TicketRepository;
import com.ticketsystem.ticketsystem.repository.UserRepository;
import com.ticketsystem.ticketsystem.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired private TicketRepository ticketRepository;
    @Autowired private UserRepository userRepository;

    // SLA thresholds
    private static final Duration HIGH_SLA = Duration.ofHours(4);
    private static final Duration MEDIUM_SLA = Duration.ofHours(24);
    private static final Duration LOW_SLA = Duration.ofHours(72);

    @Override
    public Map<String, Long> getOpenTicketsByPriority() {
        return ticketRepository.findAll().stream()
                .filter(t -> t.getStatus() != TicketStatus.RESOLVED && t.getStatus() != TicketStatus.CLOSED)
                .collect(Collectors.groupingBy(
                        t -> t.getPriority().name(),
                        Collectors.counting()
                ));
    }

    @Override
    public Map<String, Long> getTicketsByStatus() {
        return ticketRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        t -> t.getStatus().name(),
                        Collectors.counting()
                ));
    }

    @Override
    public Long getMyWorkload(Long userId) {
        return ticketRepository.findAll().stream()
                .filter(t -> t.getAssignedTo() != null && t.getAssignedTo().getId() == userId)
                .filter(t -> t.getStatus() == TicketStatus.IN_PROGRESS)
                .count();
    }

    @Override
    public ComplianceDTO getSlaCompliance() {
        List<Ticket> resolved = ticketRepository.findAll().stream()
                .filter(t -> t.getStatus() == TicketStatus.RESOLVED)
                .toList();

        long compliant = resolved.stream()
                .filter(this::isSlaCompliant)
                .count();

        ComplianceDTO dto = new ComplianceDTO();
        dto.setTotalResolved((long) resolved.size());
        dto.setSlaCompliant(compliant);
        dto.setComplianceRate(resolved.isEmpty() ? 100.0 : (compliant * 100.0 / resolved.size()));
        return dto;
    }

    private boolean isSlaCompliant(Ticket t) {
        Duration taken = Duration.between(t.getCreatedAt(), t.getUpdatedAt());
        return switch (t.getPriority()) {
            case HIGH -> taken.compareTo(HIGH_SLA) <= 0;
            case MEDIUM -> taken.compareTo(MEDIUM_SLA) <= 0;
            case LOW -> taken.compareTo(LOW_SLA) <= 0;
        };
    }

    @Override
    public Double getAverageResolutionTime() {
        List<Ticket> resolved = ticketRepository.findAll().stream()
                .filter(t -> t.getStatus() == TicketStatus.RESOLVED)
                .toList();

        if (resolved.isEmpty()) return 0.0;

        double totalHours = resolved.stream()
                .mapToDouble(t -> Duration.between(t.getCreatedAt(), t.getUpdatedAt()).toMinutes() / 60.0)
                .sum();

        return totalHours / resolved.size();
    }

    @Override
    public List<TicketTrendDTO> getTicketTrend(String period) {
        LocalDate end = LocalDate.now();
        LocalDate start = switch (period.toLowerCase()) {
            case "daily" -> end.minusDays(7);
            case "weekly" -> end.minusWeeks(4);
            case "monthly" -> end.minusMonths(6);
            default -> end.minusDays(30);
        };

        Map<LocalDate, Long> map = ticketRepository.findAll().stream()
                .filter(t -> !t.getCreatedAt().toLocalDate().isBefore(start))
                .collect(Collectors.groupingBy(
                        t -> t.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        return start.datesUntil(end.plusDays(1))
                .map(date -> {
                    TicketTrendDTO dto = new TicketTrendDTO();
                    dto.setDate(date);
                    dto.setCount(map.getOrDefault(date, 0L));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryCountDTO> getTopCategories(int limit) {
        return ticketRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().name(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(e -> {
                    CategoryCountDTO dto = new CategoryCountDTO();
                    dto.setCategory(e.getKey());
                    dto.setCount(e.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PerformanceDTO> getAgentPerformance() {
        List<User> agents = userRepository.findByRole(Role.IT_SUPPORT);

        return agents.stream().map(agent -> {
            List<Ticket> resolved = ticketRepository.findAll().stream()
                    .filter(t -> t.getStatus() == TicketStatus.RESOLVED)
                    .filter(t -> t.getAssignedTo() != null && t.getAssignedTo().equals(agent))
                    .toList();

            double avgHours = resolved.stream()
                    .mapToDouble(t -> Duration.between(t.getCreatedAt(), t.getUpdatedAt()).toMinutes() / 60.0)
                    .average()
                    .orElse(0.0);

            PerformanceDTO dto = new PerformanceDTO();
            dto.setUsername(agent.getUsername());
            dto.setResolvedCount((long) resolved.size());
            dto.setAvgResolutionTimeHours(avgHours);
            return dto;
        }).collect(Collectors.toList());
    }
}