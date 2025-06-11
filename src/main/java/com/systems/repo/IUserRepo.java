package com.systems.repo;

import com.systems.model.User;

public interface IUserRepo extends IGenericRepo<User, Integer> {
    User findOneByUsername(String username);
}
