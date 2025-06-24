package com.systems.service;

import java.util.List;
import com.systems.model.Person;

public interface IPersonService extends IGenericService<Person, Integer> {
    
    List<Person> findPersonsWhoAreTeachers() throws Exception;
    List<Person> findPersonsWhoAreStudents() throws Exception;
}
