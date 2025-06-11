package com.systems.service.impl;

import com.systems.exception.ModelNotFoundException;
import com.systems.repo.IGenericRepo;
import com.systems.service.IGenericService;

import java.util.List;

public abstract class GenericService<T, ID> implements IGenericService<T, ID> {
    protected abstract IGenericRepo<T, ID> getRepo();

    @Override
    public T save(T t) throws Exception {
        return getRepo().save(t); // Placeholder return
    }

    @Override
    public T update(T t, ID id) throws Exception {
        getRepo().findById(id).orElseThrow(()-> new ModelNotFoundException("ID NOT FOUND"+ id));
        return getRepo().save(t); // Placeholder return
    }

    @Override
    public List<T> findAll() throws Exception {
        // Implementation for finding all entities
        return getRepo().findAll(); // Placeholder return
    }

    @Override
    public T findById(ID id) throws Exception {
        // Implementation for finding an entity by ID
        return getRepo().findById(id).orElseThrow(()-> new ModelNotFoundException("ID NOT FOUND: "+ id)); // Placeholder return
    }

    @Override
    public void delete(ID id) throws Exception {
        getRepo().findById(id).orElseThrow(()-> new ModelNotFoundException("ID NOT FOUND: "+ id));
        getRepo().deleteById(id);
    }

}
