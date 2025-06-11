package com.systems.service.impl;


import org.springframework.stereotype.Service;

import com.systems.model.User;
import com.systems.repo.IGenericRepo;
import com.systems.repo.IUserRepo;
import com.systems.service.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService extends GenericService<User, Integer> implements IUserService {
    private final IUserRepo repo;

	@Override
	protected IGenericRepo<User, Integer> getRepo() {
		return repo;
	}
}
