package com.globaledge.academy.lms.user.service.impl;

import com.globaledge.academy.lms.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    // Custom getter to access User entity
    public User getUser() {
        return this.user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // We don't implement account expiration
    }

    @Override
    public boolean isAccountNonLocked() {
        // Check temporary lock status
        return !user.isAccountLockedTemporarily();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // CRITICAL: Return true here to prevent Spring Security from throwing CredentialsExpiredException
        // We handle password expiration manually in AuthenticationServiceImpl
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getAccountEnabled();
    }
}