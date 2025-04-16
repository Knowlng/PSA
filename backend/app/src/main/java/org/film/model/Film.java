package org.film.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "film_name_translations", columnDefinition = "jsonb", nullable = false)
    private Map<String, String> filmNameTranslations = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "film_desc_translations", columnDefinition = "jsonb", nullable = false)
    private Map<String, String> filmDescTranslations = new HashMap<>();

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

