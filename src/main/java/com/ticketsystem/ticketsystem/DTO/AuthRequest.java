package com.ticketsystem.ticketsystem.DTO;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
