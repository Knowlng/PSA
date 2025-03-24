package org.film.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangeUsernameRequest {
    @NotBlank
    @Size(max = 25, message = "Username must not exceed 25 characters")
    private String newUsername;

    public String getNewUsername() {
        return newUsername;
    }
    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}
