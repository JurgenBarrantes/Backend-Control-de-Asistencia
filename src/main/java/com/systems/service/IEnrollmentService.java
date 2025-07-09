package com.systems.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.systems.model.Enrollment;

public interface IEnrollmentService extends IGenericService<Enrollment, Integer> {
    Page<Enrollment> findAllWithStudentAndClassroom(Pageable pageable);

    List<Enrollment> findByStudentId(Integer studentId);

    List<Enrollment> findByClassroomId(Integer classroomId);
}
