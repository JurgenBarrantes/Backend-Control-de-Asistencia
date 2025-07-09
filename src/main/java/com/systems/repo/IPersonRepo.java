package com.systems.repo;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.systems.model.Person;

public interface IPersonRepo extends IGenericRepo<Person, Integer> {

    // Encontrar personas que son teachers (usando SQL nativo)
    @Query(value = "SELECT p.* FROM people p INNER JOIN teachers t ON p.id_person = t.id_person", nativeQuery = true)
    List<Person> findPersonsWhoAreTeachers();

    // Encontrar personas que son students (usando SQL nativo)
    @Query(value = "SELECT p.* FROM people p INNER JOIN students s ON p.id_person = s.id_person", nativeQuery = true)
    List<Person> findPersonsWhoAreStudents();

    // Métodos con fetch join para cargar User con sus roles
    @Query("SELECT p FROM Person p LEFT JOIN FETCH p.user u LEFT JOIN FETCH u.roles")
    Page<Person> findAllWithUser(Pageable pageable);

    @Query("SELECT p FROM Person p LEFT JOIN FETCH p.user u LEFT JOIN FETCH u.roles")
    List<Person> findAllWithUser();
}
