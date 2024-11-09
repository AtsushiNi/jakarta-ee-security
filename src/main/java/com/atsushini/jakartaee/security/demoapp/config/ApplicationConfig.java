package com.atsushini.jakartaee.security.demoapp.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

@ApplicationScoped
@BasicAuthenticationMechanismDefinition(realmName = "basicAuth")
@DatabaseIdentityStoreDefinition(
    dataSourceLookup = "java:/PostgresDS",
    callerQuery = "select hashed_password from users where user_name = ?",
    groupsQuery = "select role from user_roles join users on user_roles.user_id = users.user_id where user_name = ?",
    hashAlgorithm = Pbkdf2PasswordHash.class,
    hashAlgorithmParameters = {
        "Pbkdf2PasswordHash.Iterations=3072",
        "Pbkdf2PasswordHash.Algorithm=PBKDF2WithHmacSHA512",
        "Pbkdf2PasswordHash.SaltSizeBytes=64"
    }
)
public class ApplicationConfig {
    
}
