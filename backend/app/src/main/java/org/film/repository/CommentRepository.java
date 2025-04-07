package org.film.repository;

import java.util.Optional;
import org.film.model.Comment;
import org.film.model.CommentId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, CommentId> {
    Optional<Comment> findByIdFilmIdAndIdUserId(Long filmId, Long userId);
    Page<Comment> findByIdFilmId(Long filmId, Pageable pageable);
}
