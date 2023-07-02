package com.smart.controller;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/dashboard")
    public String userDashboard(Model model, Principal principal) {
        String userName = principal.getName();

        User user = userRepository.getUserByUserName(userName);

        model.addAttribute("title", "User's Dashboard");
        model.addAttribute("user", user);

        return "normal/user_dashboard";
    }
}
