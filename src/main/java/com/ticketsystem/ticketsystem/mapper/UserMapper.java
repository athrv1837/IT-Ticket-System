package com.ticketsystem.ticketsystem.mapper;

import org.mapstruct.Mapper;
import com.ticketsystem.ticketsystem.DTO.UserDTO;
import com.ticketsystem.ticketsystem.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO userToUserDTO(User user);

    User userDTOToUser(UserDTO userDTO);
}
