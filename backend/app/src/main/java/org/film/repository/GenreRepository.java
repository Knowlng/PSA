package org.film.repository;

import java.util.List;
import java.util.Optional;

import org.film.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query(value = "SELECT * FROM genre WHERE LOWER(genre_name_translations->>'en') LIKE LOWER(CONCAT('%', :query, '%'))", nativeQuery = true)
    List<Genre> findByGenreNameTranslations_EnContainingIgnoreCase(@Param("query") String query);

    @Query(value = "SELECT * FROM genre WHERE LOWER(genre_name_translations->>'lt') LIKE LOWER(CONCAT('%', :query, '%'))", nativeQuery = true)
    List<Genre> findByGenreNameTranslations_LtContainingIgnoreCase(@Param("query") String query);

    @Query(value = "SELECT * FROM genre WHERE genre_name_translations->>'en' = :enName", nativeQuery = true)
    Optional<Genre> findByGenreNameTranslations_En(@Param("enName") String enName);
}
