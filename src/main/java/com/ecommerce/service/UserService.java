package com.ecommerce.service;

import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(String name, String email, String password) {
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }

        // Optionally hash the password (recommended)
        String hashedPassword = password; // Replace with BCrypt

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }
}

