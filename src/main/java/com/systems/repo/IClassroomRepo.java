package com.systems.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.systems.model.Classroom;

public interface IClassroomRepo extends IGenericRepo<Classroom, Integer> {
    // Métodos adicionales específicos para Classroom pueden ir aquí
    
    @Query("SELECT c FROM Classroom c LEFT JOIN FETCH c.teacher LEFT JOIN FETCH c.subject")
    Page<Classroom> findAllWithTeacherAndSubject(Pageable pageable);
}
