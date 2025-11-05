package com.ticketsystem.ticketsystem.DTO;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ticketsystem.ticketsystem.enums.Role;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private Role role;
}
