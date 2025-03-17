package org.film.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FilmPersonRequest {
    @NotNull(message = "Person id is required")
    private Long personId;

    @NotBlank(message = "Role cannot be empty")
    @Size(max = 25, message = "Role must not exceed 25 characters")
    private String role;
}
