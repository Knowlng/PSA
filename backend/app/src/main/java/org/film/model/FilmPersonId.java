package org.film.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class FilmPersonId implements Serializable {
    private Long filmId;
    private Long personId;
}
