package com.smart.service;

import com.smart.entities.User;

public interface UserService {
    public User getUserByUserName(String email);

    public User addUser(User user);
}
