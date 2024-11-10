package com.atsushini.jakartaee.security.demoapp.web;

import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.security.Principal;

import com.atsushini.jakartaee.security.demoapp.config.CustomCallerPrincipal;
import com.atsushini.jakartaee.security.demoapp.dto.UserDto;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.SecurityContext;
import jakarta.servlet.http.HttpSession;
import lombok.Data;

@Named
@ViewScoped
@Data
public class HeaderBean implements Serializable {
    private UserDto currentUser;

    @Inject
    private transient ExternalContext externalContext;

    @Inject
    private transient SecurityContext securityContext;

    @PostConstruct
    public void init() {
        Principal principal = securityContext.getCallerPrincipal();
        if (principal instanceof CustomCallerPrincipal) {
            currentUser = ((CustomCallerPrincipal) principal).getUser();
        }
    }

    public void logout() {
        HttpSession session = (HttpSession) externalContext.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        try {
            externalContext.redirect("login.xhtml");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return;
    }
}
