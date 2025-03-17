package org.film.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PersonRequest {

    @NotBlank(message = "Person name cannot be empty")
    @Size(max = 50, message = "Person name must not exceed 50 characters")
    private String personFullName;

    public String getPersonFullName() {
        return personFullName;
    }

    public void setPersonFullName(String personFullName) {
        this.personFullName = personFullName;
    }
}