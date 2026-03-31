package com.pos.repository;

import com.pos.model.User;

public interface UserRepository {
    User findByUsername(String username);
    boolean save(User user);
    boolean existsByUsername(String username);
}
