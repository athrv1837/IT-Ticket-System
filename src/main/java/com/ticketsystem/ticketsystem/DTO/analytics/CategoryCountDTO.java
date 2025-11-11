package com.ticketsystem.ticketsystem.DTO.analytics;

import lombok.Data;

@Data
public class CategoryCountDTO {
    private String category;
    private Long count;
}