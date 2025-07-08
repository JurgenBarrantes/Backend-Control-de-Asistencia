package com.systems.service.impl;

import com.systems.exception.ModelNotFoundException;
import com.systems.repo.IGenericRepo;
import com.systems.service.IGenericService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    @Override
    public Page<T> findAllPaginated(int page, int size) throws Exception {
        Pageable pageable = PageRequest.of(page, size);
        return getRepo().findAll(pageable);
    }

    @Override
    public Page<T> findAllPaginated(int page, int size, String sortBy, String sortDirection) throws Exception {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return getRepo().findAll(pageable);
    }

}
