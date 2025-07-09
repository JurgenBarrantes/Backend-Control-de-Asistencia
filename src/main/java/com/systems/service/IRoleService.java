package com.systems.service;

import java.util.Optional;

import com.systems.model.Role;

public interface IRoleService extends IGenericService<Role, Integer> {
    // Buscar rol por nombre
    Optional<Role> findByName(String name);
}
