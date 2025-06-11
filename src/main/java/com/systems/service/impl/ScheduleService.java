package com.systems.service.impl;

import org.springframework.stereotype.Service;

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
}
