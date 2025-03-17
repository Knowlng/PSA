package org.film.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "film")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_id")
    @EqualsAndHashCode.Include
    private Long filmId;

    @Column(name = "film_name", length = 255, nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String filmName;

    @Column(name = "film_desc", length = 500, nullable = false)
    private String filmDesc;

    @Column(name = "film_release_date")
    private LocalDate filmReleaseDate;

    @Column(name = "film_rating", length = 5)
    private String filmRating;

    @Column(name = "film_gross")
    private Long filmGross;

    @ManyToMany
    @JoinTable(
        name = "film_genre",
        joinColumns = @JoinColumn(name = "film_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<FilmPerson> filmPersons = new HashSet<>();
}

