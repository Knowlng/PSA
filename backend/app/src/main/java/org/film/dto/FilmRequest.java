package org.film.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class FilmRequest {

    @NotBlank(message = "Film name cannot be empty")
    @Size(max = 255, message = "Film name must not exceed 255 characters")
    private String filmName;

    @NotBlank(message = "Film description cannot be empty")
    @Size(max = 500, message = "Film description must not exceed 500 characters")
    private String filmDesc;

    private LocalDate filmReleaseDate;

    @Size(max = 5, message = "Film rating must not exceed 5 characters")
    private String filmRating;

    @Max(value = 1000000000000L, message = "Film gross must not exceed 1000000000000")
    @PositiveOrZero(message = "Film gross must be zero or positive")
    private Long filmGross;

    private List<Long> genreIds;

    private List<FilmPersonRequest> persons;
}
