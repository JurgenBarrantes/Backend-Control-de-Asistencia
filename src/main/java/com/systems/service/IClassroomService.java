package com.systems.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.systems.model.Classroom;

public interface IClassroomService extends IGenericService<Classroom, Integer> {
    // Métodos adicionales específicos para Classroom pueden ir aquí
    Page<Classroom> findAllWithTeacherAndSubject(Pageable pageable);

    List<Classroom> findByTeacherId(Integer teacherId);
}
