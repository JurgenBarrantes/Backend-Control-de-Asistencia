package com.systems.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.systems.model.Classroom;

public interface IClassroomRepo extends IGenericRepo<Classroom, Integer> {
    // Métodos adicionales específicos para Classroom pueden ir aquí

    @Query("SELECT c FROM Classroom c LEFT JOIN FETCH c.teacher LEFT JOIN FETCH c.subject")
    Page<Classroom> findAllWithTeacherAndSubject(Pageable pageable);

    @Query("SELECT c FROM Classroom c WHERE c.teacher.idTeacher = :teacherId")
    List<Classroom> findByTeacherId(@Param("teacherId") Integer teacherId);

}
