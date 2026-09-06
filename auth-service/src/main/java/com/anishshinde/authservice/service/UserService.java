package com.anishshinde.authservice.service;

import com.anishshinde.authservice.model.User;
import com.anishshinde.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        // Keep database access encapsulated -> easier to extend later.
        return userRepository.findByEmail(email);
    }

}
