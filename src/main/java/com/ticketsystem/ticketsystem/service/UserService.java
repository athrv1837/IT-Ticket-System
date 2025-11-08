package com.ticketsystem.ticketsystem.service;

import java.util.List;

import com.ticketsystem.ticketsystem.DTO.UserDTO;

public interface UserService {
    UserDTO getUserByUserame(String username);
    List<UserDTO> getAllUsers();
}
