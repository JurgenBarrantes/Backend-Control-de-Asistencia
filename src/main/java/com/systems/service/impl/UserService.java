package com.systems.service.impl;

import java.util.Optional;

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

	@Override
	public Optional<User> findByUsername(String username) {
		return repo.findByUsername(username);
	}

	@Override
	public Optional<User> findByPersonEmail(String email) {
		return repo.findByPersonEmail(email);
	}

	@Override
	public Optional<User> findByUsernameOrPersonEmail(String usernameOrEmail) {
		return repo.findByUsernameOrPersonEmail(usernameOrEmail);
	}

	@Override
	public boolean existsByUsername(String username) {
		return repo.existsByUsername(username);
	}

	@Override
	public boolean existsByPersonEmail(String email) {
		return repo.existsByPersonEmail(email);
	}
}
