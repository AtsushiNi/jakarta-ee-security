package com.atsushini.jakartaee.security.demoapp.infrastructure.repository;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import com.atsushini.jakartaee.security.demoapp.dto.UserDto;
import com.atsushini.jakartaee.security.demoapp.infrastructure.entity.UserEntity;
import com.atsushini.jakartaee.security.demoapp.infrastructure.entity.UserRoleEntity;

import jakarta.enterprise.context.Dependent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Dependent
@Transactional
public class UserRepository {
    @PersistenceContext
    private EntityManager em;

    // ユーザーの作成
    public void create(UserDto userDto) {
        UserEntity user = new UserEntity();

        user.setUserName(userDto.getUserName());
        user.setPassword(userDto.getPassword());

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
}
