package org.film.repository;

import java.util.List;
import java.util.Optional;

import org.film.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FilmRepository extends JpaRepository<Film, Long>, JpaSpecificationExecutor<Film> {
    Optional<Film> findByFilmName(String filmName);
    List<Film> findByFilmNameContainingIgnoreCase(String filmName);

}