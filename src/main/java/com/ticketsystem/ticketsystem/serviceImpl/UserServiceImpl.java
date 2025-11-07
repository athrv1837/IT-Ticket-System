package com.ticketsystem.ticketsystem.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ticketsystem.ticketsystem.DTO.UserDTO;
import com.ticketsystem.ticketsystem.entity.User;
import com.ticketsystem.ticketsystem.mapper.UserMapper;
import com.ticketsystem.ticketsystem.repository.UserRepository;
import com.ticketsystem.ticketsystem.service.UserService;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDTO getUserByUserame(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User Not Found"));
        UserDTO userDTO = userMapper.userToUserDTO(user);
        return userDTO;
    }

}
