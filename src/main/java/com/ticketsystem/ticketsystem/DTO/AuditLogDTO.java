package com.ticketsystem.ticketsystem.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLogDTO {
    private Long id;
    private String entityType;
    private Long entityId;
    private String action;
    private String changes;
    private String userUsername;
    private LocalDateTime timestamp;
}