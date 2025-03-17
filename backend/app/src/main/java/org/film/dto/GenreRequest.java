package org.film.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GenreRequest {
    
    @NotBlank(message = "Genre name cannot be empty")
    @Size(max = 25, message = "Genre name must not exceed 25 characters")
    private String genreName;

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}
