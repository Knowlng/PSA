package org.film.repository;

import java.util.List;
import java.util.Optional;

import org.film.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FilmRepository extends JpaRepository<Film, Long>, JpaSpecificationExecutor<Film> {
    @Query(value = """
        SELECT *
          FROM film f
         WHERE f.film_name_translations->> 'en' = :name
        """,
        nativeQuery = true
    )
    Optional<Film> findByEnglishName(@Param("name") String name);
    
    @Query(value = """
        SELECT *
            FROM film
            WHERE LOWER(film_name_translations->>'en')
                LIKE LOWER(CONCAT('%', :query, '%'))
        """,
        nativeQuery = true
    )
    List<Film> findByFilmNameTranslations_EnContainingIgnoreCase(@Param("query") String query);

    @Query(value = """
        SELECT *
            FROM film
            WHERE LOWER(film_name_translations->>'lt')
                LIKE LOWER(CONCAT('%', :query, '%'))
        """,
        nativeQuery = true
    )
    List<Film> findByFilmNameTranslations_LtContainingIgnoreCase(@Param("query") String query);

}