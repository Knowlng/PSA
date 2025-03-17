package org.film.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "film_person")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FilmPerson {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private FilmPersonId id = new FilmPersonId();

    @ManyToOne
    @MapsId("filmId")
    @JoinColumn(name = "film_id")
    @JsonBackReference
    private Film film;

    @ManyToOne
    @MapsId("personId")
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "role", length = 25)
    private String role;
}

