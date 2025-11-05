package com.ticketsystem.ticketsystem.security;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ticketsystem.ticketsystem.DTO.AuthRequest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter{
    private final AuthenticationManager authenticationManager;
    private final Jwtutil jwtutil;

    public AuthenticationFilter(AuthenticationManager authenticationManager , Jwtutil jwtutil){
        this.authenticationManager = authenticationManager;
        this.jwtutil = jwtutil;
        setFilterProcessesUrl("/user/authenticate"); // Matches endpoint
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest , HttpServletResponse httpServletResponse){
        try{
            AuthRequest authRequest = new ObjectMapper().readValue(httpServletRequest.getInputStream(), AuthRequest.class);
            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword())
            );
        }
        catch(Exception e){
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    protected void succcessfulAuthentication(HttpServletRequest request , 
                    HttpServletResponse response , FilterChain chain , 
                    Authentication authResult) throws IOException{                                                                                                                                                                                                                                                                                                          
        String token = jwtutil.generateToken(authResult);
        response.getWriter().write(token);
        response.getWriter().flush();
    }
}
