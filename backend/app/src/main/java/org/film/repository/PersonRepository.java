package org.film.repository;

import java.util.List;
import java.util.Optional;

import org.film.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByPersonFullNameContainingIgnoreCase(String personFullName);

    Optional<Person> findByPersonFullName(String personFullName);
}
