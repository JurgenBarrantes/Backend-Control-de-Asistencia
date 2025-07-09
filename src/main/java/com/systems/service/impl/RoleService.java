package com.systems.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.systems.model.Role;
import com.systems.repo.IRoleRepo;
import com.systems.repo.IGenericRepo;
import com.systems.service.IRoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService extends GenericService<Role, Integer> implements IRoleService {
    
    private final IRoleRepo repo;

    @Override
    protected IGenericRepo<Role, Integer> getRepo() {
        return repo;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return repo.findByName(name);
    }
}
