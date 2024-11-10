package com.atsushini.jakartaee.security.demoapp.config;

import com.atsushini.jakartaee.security.demoapp.dto.UserDto;

import jakarta.security.enterprise.CallerPrincipal;
import lombok.Getter;

@Getter
public class CustomCallerPrincipal extends CallerPrincipal {
    private final UserDto user;

    public CustomCallerPrincipal(UserDto user) {
        super(user.getUserName());
        this.user = user;
    }
}
