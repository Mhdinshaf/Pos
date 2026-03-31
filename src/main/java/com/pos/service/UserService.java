package com.pos.service;

import com.pos.model.User;

public interface UserService {
    boolean register(String username, String password, String role);
    User login(String username, String password);
}
