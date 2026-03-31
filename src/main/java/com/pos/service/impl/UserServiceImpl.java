package com.pos.service.impl;

import com.pos.model.User;
import com.pos.repository.UserRepository;
import com.pos.service.UserService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean register(String username, String password, String role) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.length() < 6) {
            return false;
        }
        if (userRepository.existsByUsername(username)) {
            return false;
        }

        String hashedPassword = hashPassword(password);
        User user = new User(username, hashedPassword, role);
        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        if (password == null || password.isEmpty()) {
            return null;
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }

        String hashedPassword = hashPassword(password);
        if (user.getPassword().equals(hashedPassword)) {
            return user;
        }
        return null;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
