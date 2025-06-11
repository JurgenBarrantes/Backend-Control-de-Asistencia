package com.systems.service.impl;

import org.springframework.stereotype.Service;

import com.systems.model.Attendance;
import com.systems.repo.IAttendanceRepo;
import com.systems.repo.IGenericRepo;
import com.systems.service.IAttendanceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService extends GenericService<Attendance, Integer> implements IAttendanceService {
    private final IAttendanceRepo repo;

    @Override
    protected IGenericRepo<Attendance, Integer> getRepo() {
        return repo;
    }

}
