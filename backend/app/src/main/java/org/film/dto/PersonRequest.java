package org.film.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PersonRequest {
    
    @NotBlank(message = "English genre name cannot be empty")
    @Size(max = 50, message = "Person name must not exceed 50 characters")
    private String enPersonName;

    @NotBlank(message = "Lithuanian person name cannot be empty")
    @Size(max = 50, message = "Person name must not exceed 50 characters")
    private String ltPersonName;

    public String getEnPersonName() {
        return enPersonName;
    }

    public void setEnPersonName(String enPersonName) {
        this.enPersonName = enPersonName;
    }

    public String getLtPersonName() {
        return ltPersonName;
    }

    public void setLtPersonName(String ltPersonName) {
        this.ltPersonName = ltPersonName;
    }
}