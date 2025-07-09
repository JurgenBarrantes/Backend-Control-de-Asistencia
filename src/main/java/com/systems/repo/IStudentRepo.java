package com.systems.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.systems.model.Student;

public interface IStudentRepo extends IGenericRepo<Student, Integer> {

    @Query("SELECT s FROM Student s WHERE s.person.user.idUser = :userId")
    Optional<Student> findByPersonIdUser(@Param("userId") Integer userId);

}
