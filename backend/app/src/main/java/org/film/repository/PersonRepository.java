package org.film.repository;

import java.util.List;
import java.util.Optional;

import org.film.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query(value = "SELECT * FROM person WHERE LOWER(person_full_name_translations->>'en') LIKE LOWER(CONCAT('%', :query, '%'))", nativeQuery = true)
    List<Person> findByPersonNameTranslations_EnContainingIgnoreCase(@Param("query") String query);

    @Query(value = "SELECT * FROM person WHERE LOWER(person_full_name_translations->>'lt') LIKE LOWER(CONCAT('%', :query, '%'))", nativeQuery = true)
    List<Person> findByPersonNameTranslations_LtContainingIgnoreCase(@Param("query") String query);

    @Query(value = "SELECT * FROM person WHERE person_full_name_translations->>'en' = :enName", nativeQuery = true)
    Optional<Person> findByPersonNameTranslations_En(@Param("enName") String enName);

}
