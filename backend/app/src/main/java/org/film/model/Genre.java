package org.film.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "genre")
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "genre_id")
public class Genre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private Long genre_id;
    
    @Column(name = "genre_name", length = 25, nullable = false, unique = true)
    private String genreName;

    @ManyToMany(mappedBy = "genres")
    @EqualsAndHashCode.Exclude
    private Set<Film> films = new HashSet<>();
}
