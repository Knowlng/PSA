package org.film.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Film")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long filmId;

    private String filmName;
    private String filmDesc;
    private LocalDate filmReleaseDate;
    private String filmRating;
    private Integer filmGross;

    public Film() {}

    public Film(String filmName, String filmDesc, LocalDate filmReleaseDate, String filmRating, Integer filmGross) {
        this.filmName = filmName;
        this.filmDesc = filmDesc;
        this.filmReleaseDate = filmReleaseDate;
        this.filmRating = filmRating;
        this.filmGross = filmGross;
    }

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getFilmDesc() {
        return filmDesc;
    }

    public void setFilmDesc(String filmDesc) {
        this.filmDesc = filmDesc;
    }

    public LocalDate getFilmReleaseDate() {
        return filmReleaseDate;
    }

    public void setFilmReleaseDate(LocalDate filmReleaseDate) {
        this.filmReleaseDate = filmReleaseDate;
    }

    public String getFilmRating() {
        return filmRating;
    }

    public void setFilmRating(String filmRating) {
        this.filmRating = filmRating;
    }

    public Integer getFilmGross() {
        return filmGross;
    }

    public void setFilmGross(Integer filmGross) {
        this.filmGross = filmGross;
    }
}
