package org.film.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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

    @PrePersist
    @PreUpdate
    public void updateId() {
        id.setFilmId(film.getFilmId());
        id.setPersonId(person.getPerson_id());
    }
    
    public String getRole() {
        return id.getRole();
    }

    public void setRole(String role) {
        id.setRole(role);
    }
}

