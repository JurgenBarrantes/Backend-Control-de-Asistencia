package com.systems.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.systems.model.Enrollment;

public interface IEnrollmentRepo extends IGenericRepo<Enrollment, Integer> {

    @Query("SELECT e FROM Enrollment e " +
            "JOIN FETCH e.student s " +
            "JOIN FETCH s.person " +
            "JOIN FETCH e.classroom c " +
            "JOIN FETCH c.teacher t " +
            "JOIN FETCH t.person " +
            "JOIN FETCH c.subject")
    Page<Enrollment> findAllWithStudentAndClassroom(Pageable pageable);
}
