package com.systems.service.impl;

import org.springframework.stereotype.Service;
import com.systems.model.Student;
import com.systems.repo.IGenericRepo;
import com.systems.repo.IStudentRepo;
import com.systems.service.IStudentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService extends GenericService<Student, Integer> implements IStudentService {
    private final IStudentRepo repo;

    @Override
    protected IGenericRepo<Student, Integer> getRepo() {
        return repo;
    }
}
