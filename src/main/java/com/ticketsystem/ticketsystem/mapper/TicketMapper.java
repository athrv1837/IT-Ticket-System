package com.ticketsystem.ticketsystem.mapper;

import org.mapstruct.Mapper;

import com.ticketsystem.ticketsystem.DTO.TicketDTO;
import com.ticketsystem.ticketsystem.entity.Ticket;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    Ticket ticketDTOToTicket(Ticket ticket);
    TicketDTO ticketToTicketDTO(Ticket ticket);
}
