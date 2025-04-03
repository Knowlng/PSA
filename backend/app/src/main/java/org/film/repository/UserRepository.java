package org.film.repository;

import java.util.List;
import java.util.Optional;

import org.film.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    List<User> findByUserNameContainingIgnoreCase(String query);
}