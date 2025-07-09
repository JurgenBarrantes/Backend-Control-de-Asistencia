package com.systems.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.systems.model.Classroom;
import com.systems.model.Schedule;
import com.systems.repo.IGenericRepo;
import com.systems.repo.IScheduleRepo;
import com.systems.service.IScheduleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService extends GenericService<Schedule, Integer> implements IScheduleService {
    private final IScheduleRepo repo;

	@Override
	protected IGenericRepo<Schedule, Integer> getRepo() {
		return repo;
	}

	@Override
	public List<Schedule> findByClassroom(Classroom classroom) {
		return repo.findByClassroom(classroom);
	}

	@Override
	public Page<Schedule> findAllWithClassroom(Pageable pageable) {
		return repo.findAllWithClassroom(pageable);
	}
}
