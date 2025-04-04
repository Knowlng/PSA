package org.film.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class UserFilmId implements Serializable {
    private Long userId;
    private Long filmId;
}
