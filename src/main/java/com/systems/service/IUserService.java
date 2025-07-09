package com.systems.service;

import java.util.Optional;

import com.systems.model.User;

public interface IUserService extends IGenericService<User, Integer> {
    // Additional methods specific to User can be defined here if needed
    
    // Métodos para autenticación
    Optional<User> findByUsername(String username);
    Optional<User> findByPersonEmail(String email);
    Optional<User> findByUsernameOrPersonEmail(String usernameOrEmail);
    boolean existsByUsername(String username);
    boolean existsByPersonEmail(String email);
}
