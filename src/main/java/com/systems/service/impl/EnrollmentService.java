package com.systems.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<Enrollment> findAllWithStudentAndClassroom(Pageable pageable) {
        return repo.findAllWithStudentAndClassroom(pageable);
    }

    @Override
    public List<Enrollment> findByStudentId(Integer studentId) {
        return repo.findByStudentId(studentId);
    }

    @Override
    public List<Enrollment> findByClassroomId(Integer classroomId) {
        return repo.findByClassroom_IdClassroom(classroomId);
    }
}
