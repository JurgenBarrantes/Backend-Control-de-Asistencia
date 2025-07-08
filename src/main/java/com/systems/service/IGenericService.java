package com.systems.service;

import java.util.List;

import org.springframework.data.domain.Page;

public interface IGenericService<T, ID> {
    T save(T t) throws Exception;
    T update(T t, ID id) throws Exception;
    List<T> findAll() throws Exception;
    T findById(ID id) throws Exception;
    void delete(ID id) throws Exception;
    
    // Métodos de paginación
    Page<T> findAllPaginated(int page, int size) throws Exception;
    Page<T> findAllPaginated(int page, int size, String sortBy, String sortDirection) throws Exception;
}
