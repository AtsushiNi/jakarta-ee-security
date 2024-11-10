package com.atsushini.jakartaee.security.demoapp.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.atsushini.jakartaee.security.demoapp.dto.UserDto;
import com.atsushini.jakartaee.security.demoapp.infrastructure.entity.Role;
import com.atsushini.jakartaee.security.demoapp.infrastructure.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

@ApplicationScoped
public class DatabaseIdentityStore implements IdentityStore {
    @Inject
    private Pbkdf2PasswordHash passwordHash;

    @Inject
    private transient UserRepository userRepository;

    @PostConstruct
    public void init() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("Pbkdf2PasswordHash.Iterations", "3072");
        parameters.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA512");
        parameters.put("Pbkdf2PasswordHash.SaltSizeBytes", "64");
        passwordHash.initialize(parameters);
    }
    
    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (credential instanceof UsernamePasswordCredential) {
            return validate((UsernamePasswordCredential) credential);
        }

        return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }

    public CredentialValidationResult validate(UsernamePasswordCredential usernamePasswordCredential) {
        UserDto user = userRepository.findByName(usernamePasswordCredential.getCaller());
        if (user == null) {
            return CredentialValidationResult.INVALID_RESULT;
        } 

        if (passwordHash.verify(usernamePasswordCredential.getPassword().getValue(), user.getHashedPassword())) {
            Set<String> roles = user.getRoles().stream().map(Role::toString).collect(Collectors.toSet());
            return new CredentialValidationResult(new CustomCallerPrincipal(user), roles);
        }

        return CredentialValidationResult.INVALID_RESULT;
    }

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        return validationResult.getCallerGroups();
    }
}
