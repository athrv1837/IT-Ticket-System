package com.ticketsystem.ticketsystem.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ticketsystem.ticketsystem.DTO.UserDTO;
import com.ticketsystem.ticketsystem.entity.User;
import com.ticketsystem.ticketsystem.repository.UserRepository;
import com.ticketsystem.ticketsystem.service.UserService;
import com.ticketsystem.ticketsystem.utils.DTOMapper;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DTOMapper dtoMapper;

    @Override
    public UserDTO getUserByUserame(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User Not Found"));
        UserDTO userDTO = dtoMapper.toUserDTO(user);
        return userDTO;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User>list = userRepository.findAll();
        if(list.isEmpty())
            throw new RuntimeException("No Users Found");

        return dtoMapper.toUserList(list);
    }

}
