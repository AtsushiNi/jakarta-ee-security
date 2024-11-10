package com.atsushini.jakartaee.security.demoapp.infrastructure.repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.atsushini.jakartaee.security.demoapp.dto.UserDto;
import com.atsushini.jakartaee.security.demoapp.infrastructure.entity.UserEntity;
import com.atsushini.jakartaee.security.demoapp.infrastructure.entity.UserRoleEntity;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.transaction.Transactional;

@Dependent
@Transactional
public class UserRepository {
    @PersistenceContext
    private EntityManager em;

    @Inject
    private Pbkdf2PasswordHash passwordHash;

    @PostConstruct
    public void init() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("Pbkdf2PasswordHash.Iterations", "3072");
        parameters.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA512");
        parameters.put("Pbkdf2PasswordHash.SaltSizeBytes", "64");
        passwordHash.initialize(parameters);
    }

    // ユーザーの作成
    public void create(UserDto userDto) {
        UserEntity user = new UserEntity();
        user.setUserName(userDto.getUserName());

        String hashedPassword = passwordHash.generate(userDto.getPassword().toCharArray());
        user.setHashedPassword(hashedPassword);

        List<UserRoleEntity> userRoles = userDto.getRoles().stream().map(role -> {
            UserRoleEntity userRole = new UserRoleEntity();
            userRole.setRole(role);
            return userRole;
        }).toList();
        user.setUserRoles(userRoles);

        em.persist(user);
    }

    // 全ユーザーの検索
    public List<UserDto> findAll() {
        Stream<UserEntity> users = em.createQuery("select u from UserEntity u", UserEntity.class).getResultStream();

        // EntityをDtoに詰め替えて返す
        return users.map(user -> {
            UserDto dto = new UserDto();
            dto.setUserName(user.getUserName());
            dto.setRoles(new HashSet<>(user.getUserRoles().stream().map(UserRoleEntity::getRole).toList()));
            return dto;
        }).toList();
    }

    public UserDto findByName(String userName) {
        TypedQuery<UserEntity> query = em.createQuery("select u from UserEntity u where u.userName = :userName", UserEntity.class);
        query.setParameter("userName", userName);
        UserEntity user = query.getSingleResult();
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setUserName(user.getUserName());
        dto.setRoles(new HashSet<>(user.getUserRoles().stream().map(UserRoleEntity::getRole).toList()));
        dto.setHashedPassword(user.getHashedPassword());
        return dto;
    }
}
