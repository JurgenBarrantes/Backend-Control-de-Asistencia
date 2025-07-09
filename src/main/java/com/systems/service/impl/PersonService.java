package com.systems.service.impl;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.systems.model.Person;
import com.systems.repo.IGenericRepo;
import com.systems.repo.IPersonRepo;
import com.systems.service.IPersonService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonService extends GenericService<Person, Integer> implements IPersonService {
    private final IPersonRepo repo;

    @Override
    protected IGenericRepo<Person, Integer> getRepo() {
        return repo;
    }

    @Override
    public List<Person> findPersonsWhoAreTeachers() throws Exception {
        return repo.findPersonsWhoAreTeachers();
    }

    @Override
    public List<Person> findPersonsWhoAreStudents() throws Exception {
        return repo.findPersonsWhoAreStudents();
    }

    @Override
    public Page<Person> findAllWithUser(Pageable pageable) throws Exception {
        return repo.findAllWithUser(pageable);
    }

    @Override
    public List<Person> findAllWithUser() throws Exception {
        return repo.findAllWithUser();
    }
}
