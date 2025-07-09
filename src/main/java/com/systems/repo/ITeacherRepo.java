package com.systems.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.systems.model.Teacher;

public interface ITeacherRepo extends IGenericRepo<Teacher, Integer> {

    @Query("SELECT t FROM Teacher t WHERE t.person.user.idUser = :userId")
    Optional<Teacher> findByPersonIdUser(@Param("userId") Integer userId);

}
