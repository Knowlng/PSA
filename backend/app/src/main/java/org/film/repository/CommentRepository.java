package org.film.repository;

import java.util.List;
import java.util.Optional;

import org.film.model.Comment;
import org.film.model.CommentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, CommentId> {
    Optional<Comment> findByIdFilmIdAndIdUserId(Long filmId, Long userId);
    List<Comment> findByIdFilmId(Long filmId);
}
