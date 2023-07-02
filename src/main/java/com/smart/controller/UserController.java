package com.smart.controller;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();

        User user = userRepository.getUserByUserName(userName);

        model.addAttribute("user", user);
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String userDashboard(Model model) {
        model.addAttribute("title", "User's Dashboard");
        return "normal/user_dashboard";
    }

    @RequestMapping(value = "/add-contact", method = RequestMethod.GET)
    public String addContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());

        return "normal/add-contact-form";
    }
}
