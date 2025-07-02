package com.systems.service.impl;

import org.springframework.stereotype.Service;

import com.systems.model.Enrollment;
import com.systems.repo.IEnrollmentRepo;
import com.systems.repo.IGenericRepo;
import com.systems.service.IEnrollmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentService extends GenericService<Enrollment, Integer> implements IEnrollmentService {
    
    private final IEnrollmentRepo repo;

    @Override
    protected IGenericRepo<Enrollment, Integer> getRepo() {
        return repo;
    }
}
