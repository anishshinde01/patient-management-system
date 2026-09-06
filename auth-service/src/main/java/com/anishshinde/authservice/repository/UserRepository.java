package com.anishshinde.authservice.repository;

import com.anishshinde.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Spring Data JPA derives the database query from the method name
    Optional<User> findByEmail(String email);
}
