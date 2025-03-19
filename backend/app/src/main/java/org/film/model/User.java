package org.film.model;

import jakarta.persistence.*;
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
}

