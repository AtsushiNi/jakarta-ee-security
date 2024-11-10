package com.atsushini.jakartaee.security.demoapp.web;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.Password;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Named
@ViewScoped
@Data
@NoArgsConstructor
public class LoginBean implements Serializable {
    private String userName;
    private String password;

    private static final Logger logger = Logger.getLogger(LoginBean.class.getName());

    @Inject
    private transient SecurityContext securityContext;

    @Inject
    private transient ExternalContext externalContext;

    @Inject
    private transient FacesContext facesContext;

    public void login() {
        Credential credential = new UsernamePasswordCredential(userName, new Password(password));
        AuthenticationStatus status = securityContext.authenticate((HttpServletRequest) externalContext.getRequest(), (HttpServletResponse) externalContext.getResponse(), AuthenticationParameters.withParams().credential(credential));

        if (status.equals(AuthenticationStatus.SEND_CONTINUE)) {
            logger.info("success authentication");
            facesContext.responseComplete();
        } else if (status.equals(AuthenticationStatus.SEND_FAILURE)) {
            logger.log(Level.SEVERE, "failed authentication");
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Authentication failed", null));
        }
    }
}
