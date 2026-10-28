package com.healthcare.hospital.service;

import com.healthcare.hospital.entity.User;
import com.healthcare.hospital.enums.RoleType;
import com.healthcare.hospital.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public User registerUser(String username, String rawPassword, String fullName, RoleType role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username/Email already exists: " + username);
        }
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(username, encodedPassword, fullName, role);
        return userRepository.save(user);
    }
}
