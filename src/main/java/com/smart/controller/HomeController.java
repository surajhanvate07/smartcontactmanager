package com.smart.controller;

import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signup(Model model) {
        model.addAttribute("title", "SignUp - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String userRegister(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model, @RequestParam(value = "terms", defaultValue = "false") boolean terms, HttpSession session) {
        try {
            if (!terms) {
                throw new Exception("Please agree to the Terms and Conditions!");
            }

            if (bindingResult.hasErrors()) {
                model.addAttribute("user", user);
                return "signup";
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            User savedUser = userService.addUser(user);

            model.addAttribute("user", new User());

            session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));

            return "signup";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));

            return "signup";
        }
    }

    @RequestMapping(value = "/signin", method = RequestMethod.GET)
    public String customLoginForm(Model model) {
        model.addAttribute("title", "Login Page");

        return "login";
    }
}
