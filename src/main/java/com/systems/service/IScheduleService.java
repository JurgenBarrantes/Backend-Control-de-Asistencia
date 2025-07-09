package com.systems.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.systems.model.Classroom;
import com.systems.model.Schedule;

public interface IScheduleService extends IGenericService<Schedule, Integer> {
    List<Schedule> findByClassroom(Classroom classroom);
    Page<Schedule> findAllWithClassroom(Pageable pageable);
}
