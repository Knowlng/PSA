package org.film.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FilmRequest {

    @NotBlank(message = "English film name cannot be empty")
    @Size(max = 255, message = "English film name must not exceed 255 characters")
    private String filmNameEn;

    @NotBlank(message = "Lithuanian film name cannot be empty")
    @Size(max = 255, message = "Lithuanian film name must not exceed 255 characters")
    private String filmNameLt;

    @NotBlank(message = "English film description cannot be empty")
    @Size(max = 500, message = "English film description must not exceed 500 characters")
    private String filmDescEn;

    @NotBlank(message = "Lithuanian film description cannot be empty")
    @Size(max = 500, message = "Lithuanian film description must not exceed 500 characters")
    private String filmDescLt;

    private LocalDate filmReleaseDate;

    @Size(max = 5, message = "Film rating must not exceed 5 characters")
    private String filmRating;

    @Max(value = 1000000000000L, message = "Film gross must not exceed 1000000000000")
    @PositiveOrZero(message = "Film gross must be zero or positive")
    private Long filmGross;

    private List<Long> genreIds;

    private List<FilmPersonRequest> persons;
}
