package com.systems.service;

import java.util.Optional;

import com.systems.model.Teacher;

public interface ITeacherService extends IGenericService<Teacher, Integer> {
    Optional<Teacher> findByPersonIdUser(Integer userId);
}
