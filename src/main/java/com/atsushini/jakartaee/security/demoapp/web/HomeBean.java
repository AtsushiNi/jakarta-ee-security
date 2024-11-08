package com.atsushini.jakartaee.security.demoapp.web;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import com.atsushini.jakartaee.security.demoapp.dto.UserDto;
import com.atsushini.jakartaee.security.demoapp.infrastructure.entity.Role;
import com.atsushini.jakartaee.security.demoapp.infrastructure.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;

@Named
@Data
@ViewScoped
public class HomeBean implements Serializable {
    private List<UserDto> users;
    private UserDto newUser;

    @Inject
    private transient UserRepository userRepository;

    @Inject
    private ExternalContext externalContext;

    @PostConstruct
    public void init() {
        users = userRepository.findAll();
        newUser = new UserDto();
    }

    public void createUser() {
        userRepository.create(newUser);

        try {
            externalContext.redirect("home.xhtml"); // リダイレクト実行
        } catch (IOException e) {
            e.printStackTrace(); // エラーハンドリング
        }
    }

    public Role[] getRoles() {
        return Role.values();
    }
}
