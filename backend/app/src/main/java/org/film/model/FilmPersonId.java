package org.film.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class FilmPersonId implements Serializable {
    private Long filmId;
    private Long personId;
    private String role;
}
