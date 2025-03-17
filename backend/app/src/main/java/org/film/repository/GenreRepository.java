package org.film.repository;

import java.util.List;
import java.util.Optional;

import org.film.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    List<Genre> findByGenreNameContainingIgnoreCase(String genreName);
    
    Optional<Genre> findByGenreName(String genreName);
}
