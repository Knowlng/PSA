package org.film.repository;

import java.util.List;
import java.util.Optional;
import org.film.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmRepository extends JpaRepository<Film, Long> {
    Optional<Film> findByFilmName(String filmName);
    List<Film> findByFilmNameContainingIgnoreCase(String filmName);

}