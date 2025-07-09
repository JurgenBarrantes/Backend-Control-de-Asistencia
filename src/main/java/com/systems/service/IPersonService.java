package com.systems.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.systems.model.Person;

public interface IPersonService extends IGenericService<Person, Integer> {

    List<Person> findPersonsWhoAreTeachers() throws Exception;

    List<Person> findPersonsWhoAreStudents() throws Exception;

    // Métodos para cargar Person con User
    Page<Person> findAllWithUser(Pageable pageable) throws Exception;

    List<Person> findAllWithUser() throws Exception;
}
