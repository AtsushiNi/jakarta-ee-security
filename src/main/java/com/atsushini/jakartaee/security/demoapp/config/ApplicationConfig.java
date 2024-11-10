package com.atsushini.jakartaee.security.demoapp.config;

import java.io.Serializable;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;

@ApplicationScoped
@CustomFormAuthenticationMechanismDefinition(
    loginToContinue = @LoginToContinue(
        errorPage = "/login_error.xhtml",
        loginPage = "/login.xhtml"
    )
)
public class ApplicationConfig implements Serializable {
    
}
