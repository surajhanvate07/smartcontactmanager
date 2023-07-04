package com.smart.service;

import com.smart.entities.User;

import java.util.Optional;

public interface UserService {
    public User getUserByUserName(String email);

    public User addUser(User user);

    public Optional<User> getUserById(int id);

    public void saveUser(User user);
}
