package com.smart.service.impl;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserByUserName(String email) {
        return userRepository.getUserByUserName(email);
    }

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }
}
