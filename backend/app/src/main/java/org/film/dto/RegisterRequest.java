package org.film.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Username cannot be empty")
    @Size(max = 25, message = "Username must not exceed 25 characters")
    private String userName;

    @NotBlank(message = "Password cannot be empty")
    @Size(max = 255, message = "Password must not exceed 255 characters")
    private String userPassword;

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserPassword() {
        return userPassword;
    }
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}

