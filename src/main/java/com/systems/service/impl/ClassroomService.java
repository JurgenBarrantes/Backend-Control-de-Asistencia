package com.systems.service.impl;

import org.springframework.stereotype.Service;

import com.systems.model.Classroom;
import com.systems.repo.IClassroomRepo;
import com.systems.repo.IGenericRepo;
import com.systems.service.IClassroomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassroomService extends GenericService<Classroom, Integer> implements IClassroomService {
    
    private final IClassroomRepo repo;

    @Override
    protected IGenericRepo<Classroom, Integer> getRepo() {
        return repo;
    }
}
