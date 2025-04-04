package org.film.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_film")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserFilm {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private UserFilmId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("filmId")
    @JoinColumn(name = "film_id")
    private Film film;

    @Column(name = "rating")
    private Integer rating;
}
