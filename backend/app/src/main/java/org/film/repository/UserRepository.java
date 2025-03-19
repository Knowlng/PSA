package org.film.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.film.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
}