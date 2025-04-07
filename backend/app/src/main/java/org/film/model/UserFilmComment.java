package org.film.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_film_comment")
@Data
public class UserFilmComment {

    @EmbeddedId
    private UserFilmCommentId id;

    @Column(name = "film_id", nullable = false)
    private Integer filmId;

    @Column(name = "comment_rating")
    private Boolean commentRating;
}

