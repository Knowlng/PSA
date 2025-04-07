package org.film.repository;

import java.util.List;

import org.film.model.UserFilmComment;
import org.film.model.UserFilmCommentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFilmCommentRepository extends JpaRepository<UserFilmComment, UserFilmCommentId> {
    List<UserFilmComment> findByIdCommentUserIdAndIdCommentFilmId(Integer commentUserId, Integer commentFilmId);
}
