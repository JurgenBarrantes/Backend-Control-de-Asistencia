package com.systems.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

import com.systems.model.Person;

public interface IPersonRepo extends IGenericRepo<Person, Integer> {
    
    // Encontrar personas que son teachers (usando SQL nativo)
    @Query(value = "SELECT p.* FROM people p INNER JOIN teachers t ON p.id_person = t.id_person", nativeQuery = true)
    List<Person> findPersonsWhoAreTeachers();
    
    // Encontrar personas que son students (usando SQL nativo)
    @Query(value = "SELECT p.* FROM people p INNER JOIN students s ON p.id_person = s.id_person", nativeQuery = true)
    List<Person> findPersonsWhoAreStudents();
}
