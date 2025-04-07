package org.film.repository;

import java.util.List;
import java.util.Optional;

import org.film.model.UserFilm;
import org.film.model.UserFilmId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFilmRepository extends JpaRepository<UserFilm, UserFilmId> {
    List<UserFilm> findByIdFilmId(Long filmId);
    
    @Override
    Optional<UserFilm> findById(UserFilmId id);
}
