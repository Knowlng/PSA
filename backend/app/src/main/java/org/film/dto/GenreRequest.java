package org.film.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GenreRequest {
    
    @NotBlank(message = "English genre name cannot be empty")
    @Size(max = 25, message = "Genre name must not exceed 25 characters")
    private String enGenreName;

    @NotBlank(message = "Lithuanian genre name cannot be empty")
    @Size(max = 25, message = "Genre name must not exceed 25 characters")
    private String ltGenreName;
}
