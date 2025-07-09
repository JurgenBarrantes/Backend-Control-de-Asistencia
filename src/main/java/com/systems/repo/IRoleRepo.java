package com.systems.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.systems.model.Role;

public interface IRoleRepo extends IGenericRepo<Role, Integer> {
    // Buscar rol por nombre - tomar el primer resultado
    @Query("SELECT r FROM Role r WHERE r.name = :name")
    List<Role> findByNameList(@Param("name") String name);
    
    // Método por defecto que toma el primer resultado
    default Optional<Role> findByName(String name) {
        List<Role> roles = findByNameList(name);
        return roles.isEmpty() ? Optional.empty() : Optional.of(roles.get(0));
    }
}
