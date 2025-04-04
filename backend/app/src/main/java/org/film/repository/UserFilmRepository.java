package org.film.repository;

import java.util.List;

import org.film.model.UserFilm;
import org.film.model.UserFilmId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFilmRepository extends JpaRepository<UserFilm, UserFilmId> {
    List<UserFilm> findByIdFilmId(Long filmId);
}
