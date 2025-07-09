package com.systems.service;

import java.util.Optional;

import com.systems.model.Student;

public interface IStudentService extends IGenericService<Student, Integer> {
    Optional<Student> findByPersonIdUser(Integer userId);
}
