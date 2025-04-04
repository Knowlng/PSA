package org.film.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RateFilmRequest {
    
    @NotNull(message = "Film ID cannot be null")
    private Long filmId;
    
    @NotNull(message = "Rating cannot be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 10, message = "Rating must be at most 10")
    private Integer rating;

    public Long getFilmId() {
        return filmId;
    }
    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }
    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
