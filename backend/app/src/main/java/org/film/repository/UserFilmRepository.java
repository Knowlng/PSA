package org.film.repository;

import org.film.model.UserFilm;
import org.film.model.UserFilmId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFilmRepository extends JpaRepository<UserFilm, UserFilmId> {
}
