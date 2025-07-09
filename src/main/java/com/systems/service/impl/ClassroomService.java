package com.systems.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<Classroom> findAllWithTeacherAndSubject(Pageable pageable) {
        return repo.findAllWithTeacherAndSubject(pageable);
    }

    @Override
    public List<Classroom> findByTeacherId(Integer teacherId) {
        return repo.findByTeacherId(teacherId);
    }
}