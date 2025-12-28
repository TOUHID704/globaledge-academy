package com.globaledge.academy.lms.security.config;

import com.globaledge.academy.lms.core.constants.SecurityConstants;
import com.globaledge.academy.lms.security.jwt.JwtAuthenticationEntryPoint;
import com.globaledge.academy.lms.security.jwt.filter.JwtFilter;
import com.globaledge.academy.lms.user.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                // ❗ DO NOT TOUCH — kept exactly as-is
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(SecurityConstants.PUBLIC_URLS).permitAll()
//
//                        //  ADMIN-ONLY ENDPOINTS
//                        // Employee Import APIs
//                        .requestMatchers(HttpMethod.POST, "/employees/import/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.GET, "/employees/import/history/**").hasRole("ADMIN")
//
//                        // Course Management APIs (Create, Update, Delete, Publish)
//                        .requestMatchers(HttpMethod.POST, "/courses/createCourse").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/courses/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/courses/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.POST, "/courses/*/publish").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.POST, "/courses/*/unpublish").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.POST, "/courses/*/execute-immediate-rules").hasRole("ADMIN")
//
//                        // Assignment Rule APIs (All operations)
//                        .requestMatchers("/assignment/rules/**").hasRole("ADMIN")
//
//                        //  USER ENDPOINTS (Read-only access to courses)
//                        .requestMatchers(HttpMethod.GET, "/courses/**").authenticated()
//
//                        //  ENROLLMENT ENDPOINTS (Users can manage their own enrollments)
//                        .requestMatchers("/enrollments/**").authenticated()
//
//                        .anyRequest().authenticated()
//                )
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                // ✅ ACTIVE CONFIG (only this part changed)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(basicAuth -> basicAuth.disable());

        return http.build();
    }
}
