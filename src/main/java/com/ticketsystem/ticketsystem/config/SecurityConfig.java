package com.ticketsystem.ticketsystem.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ticketsystem.ticketsystem.security.AuthenticationFilter;
import com.ticketsystem.ticketsystem.security.CustomAccessDeniedHandler;
import com.ticketsystem.ticketsystem.security.CustomAuthenticationEntryPoint;
import com.ticketsystem.ticketsystem.security.CustomAuthenticationManager;
import com.ticketsystem.ticketsystem.security.ExceptionHandlerFilter;
import com.ticketsystem.ticketsystem.security.JWTAuthFilter;
import com.ticketsystem.ticketsystem.security.Jwtutil;
import com.ticketsystem.ticketsystem.serviceImpl.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private Jwtutil jwtUtil;

    // @Autowired
    // private UserServiceImpl userService;

    // Used constructor injection to avoid circular dependency
    private final UserDetailsServiceImpl userService;

    public SecurityConfig(UserDetailsServiceImpl userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    // Was causing circular dependency
    // @Autowired
    // private PasswordEncoder passwordEncoder;
    // @Bean
    // public PasswordEncoder passwordEncoder() {
    // return new BCryptPasswordEncoder();
    // }

    @Bean
    public CustomAuthenticationManager customAuthenticationManager() {
        return new CustomAuthenticationManager(userService, passwordEncoder);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exc -> exc
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()
                        // Public Endpoint for Authentication
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/forgot-password", "/api/auth/reset-password").permitAll()

                        .requestMatchers(HttpMethod.PATCH, "/api/tickets/*/assign")
                        .hasAnyAuthority("ROLE_IT_SUPPORT", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("IT_SUPPORT")
                        .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                        .requestMatchers("/api/users", "/api/users/**").hasAnyRole("IT_SUPPORT", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/tickets/create").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/tickets/my-tickets").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.PATCH, "/api/tickets/*/info").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.DELETE, "/api/tickets/*").hasRole("EMPLOYEE")
                        .requestMatchers("/api/tickets", "/api/tickets/**").hasRole("IT_SUPPORT")
                        .requestMatchers(HttpMethod.PATCH, "/api/tickets/*/status").hasRole("IT_SUPPORT")
                        .requestMatchers(HttpMethod.GET, "/api/tickets", "/api/tickets/**")
                        .hasAnyRole("IT_SUPPORT", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/tickets/*/comments")
                        .hasAnyRole("EMPLOYEE", "IT_SUPPORT")
                        .requestMatchers(HttpMethod.PATCH, "/api/comments/*").hasAnyRole("EMPLOYEE", "IT_SUPPORT")
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/*").hasAnyRole("EMPLOYEE", "IT_SUPPORT")
                        .requestMatchers(HttpMethod.GET, "/api/tickets/*/comments")
                        .hasAnyRole("EMPLOYEE", "IT_SUPPORT", "MANAGER")
                        .requestMatchers("/api/tickets/*/audit").hasAnyRole("IT_SUPPORT", "MANAGER")
                        .requestMatchers("/api/analytics/my-workload").hasAnyRole("IT_SUPPORT")
                        .requestMatchers("/api/analytics/**").hasRole("MANAGER")
                        .anyRequest().authenticated())
                .addFilterBefore(new ExceptionHandlerFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new AuthenticationFilter(customAuthenticationManager(), jwtUtil),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTAuthFilter(jwtUtil, userService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "http://localhost:3000",
            "http://localhost:8081",
            "http://127.0.0.1:5173"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
