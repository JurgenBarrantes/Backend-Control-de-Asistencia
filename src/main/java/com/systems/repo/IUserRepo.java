package com.systems.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.systems.model.User;

public interface IUserRepo extends IGenericRepo<User, Integer> {
    User findOneByUsername(String username);
    
    // Métodos para autenticación
    Optional<User> findByUsername(String username);
    
    @Query("SELECT u FROM User u JOIN u.person p WHERE p.email = :email")
    Optional<User> findByPersonEmail(@Param("email") String email);
    
    @Query("SELECT u FROM User u LEFT JOIN u.person p WHERE u.username = :usernameOrEmail OR p.email = :usernameOrEmail")
    Optional<User> findByUsernameOrPersonEmail(@Param("usernameOrEmail") String usernameOrEmail);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.person p WHERE p.email = :email")
    boolean existsByPersonEmail(@Param("email") String email);
}
