package com.systems.service.impl;

import org.springframework.stereotype.Service;

import com.systems.model.Subject;
import com.systems.repo.IGenericRepo;
import com.systems.repo.ISubjectRepo;
import com.systems.service.ISubjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubjectService extends GenericService<Subject, Integer> implements ISubjectService {
    private final ISubjectRepo repo;

	@Override
	protected IGenericRepo<Subject, Integer> getRepo() {
		return repo;
	}
}
