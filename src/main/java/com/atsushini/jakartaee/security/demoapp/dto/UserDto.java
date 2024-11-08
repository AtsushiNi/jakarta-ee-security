package com.atsushini.jakartaee.security.demoapp.dto;

import java.util.HashSet;
import java.util.Set;

import com.atsushini.jakartaee.security.demoapp.infrastructure.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Integer userId;
    private String userName;
    private String password;
    private Set<Role> roles = new HashSet<>();

    public String getRoleNames() {
        return String.join(", ", roles.stream().map(Role::toString).toList());
    }
}
