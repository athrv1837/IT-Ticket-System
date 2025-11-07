package com.ticketsystem.ticketsystem.service;

import com.ticketsystem.ticketsystem.DTO.UserDTO;

public interface UserService {
    UserDTO getUserByUserame(String username);
}
