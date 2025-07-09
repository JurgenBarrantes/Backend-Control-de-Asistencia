package com.systems.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.systems.model.Teacher;
import com.systems.repo.IGenericRepo;
import com.systems.repo.ITeacherRepo;
import com.systems.service.ITeacherService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherService extends GenericService<Teacher, Integer> implements ITeacherService {
	private final ITeacherRepo repo;

	@Override
	protected IGenericRepo<Teacher, Integer> getRepo() {
		return repo;
	}

	@Override
	public Optional<Teacher> findByPersonIdUser(Integer userId) {
		return repo.findByPersonIdUser(userId);
	}
}
