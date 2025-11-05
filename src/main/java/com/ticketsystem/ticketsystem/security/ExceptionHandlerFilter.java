package com.ticketsystem.ticketsystem.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

public class ExceptionHandlerFilter extends OncePerRequestFilter{
    @Override
    protected void doFilterInternal(HttpServletRequest request , 
        HttpServletResponse response , FilterChain chain) throws ServletException , IOException{
            try{
                chain.doFilter(request, response);
            }
            catch(RuntimeException e){
                Map<String , String>error = new HashMap<>();
                error.put("error",e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("appplication/json");
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
}
