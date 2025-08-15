package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> authenticate(String accessKey) {
        return userRepository.findFirstByAccessKey(accessKey);
    }

    public boolean hasRole(User user, String role) {
        return user.getRoles() != null && Arrays.asList(user.getRoles().split(",")).contains(role);
    }
}
