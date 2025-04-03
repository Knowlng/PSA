package org.film.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "p_user")
@Data
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @Column(name = "user_name", unique = true, length = 25)
    private String userName;
    
    @Column(name = "user_password", length = 255)
    private String userPassword;
    
    @Column(name = "user_role", length = 25)
    private String userRole;
    
    @Column(name = "user_enabled", nullable = false)
    private Boolean enabled = true;
}