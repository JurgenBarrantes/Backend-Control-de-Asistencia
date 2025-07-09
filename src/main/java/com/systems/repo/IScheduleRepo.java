package com.systems.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.systems.model.Classroom;
import com.systems.model.Schedule;

public interface IScheduleRepo extends IGenericRepo<Schedule, Integer> {
    List<Schedule> findByClassroom(Classroom classroom);
    
    @Query("SELECT s FROM Schedule s LEFT JOIN FETCH s.classroom")
    Page<Schedule> findAllWithClassroom(Pageable pageable);
}
