package com.smart.controller;

import com.smart.entities.User;
import com.smart.service.EmailService;
import com.smart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "/forgot", method = RequestMethod.GET)
    public String showForgotPassword(Model model) {
        model.addAttribute("title", "Forgot Password");
        return "forgot-password";
    }

    @RequestMapping(value = "/send-otp", method = RequestMethod.POST)
    public String sendOTPHandler(@RequestParam("username") String username, Model model) {
        User loggedUser = userService.getUserByUserName(username);

        if (loggedUser == null) {
            model.addAttribute("error", "");
            return "redirect:/forgot";
        }

        model.addAttribute("title", "OTP Verification");
        model.addAttribute("user", loggedUser);

        int min = 1000; // Minimum value of range
        int max = 9999; // Maximum value of range
        int otp = (int) Math.floor(Math.random() * (max - min + 1) + min);

        String subject = "OTP From Smart Contact Manage Application";
        String message = "<h1> OTP = " + otp + " </h1";

        boolean emailFlag = emailService.sendEmail(subject, message, username);
        System.out.println(emailFlag);

        if (emailFlag) {
            return "verify-otp";
        } else {
            return "redirect:/forgot";
        }
    }

    @RequestMapping(value = "/authorized-otp", method = RequestMethod.GET)
    public String changePasswordHandler(Model model) {
        model.addAttribute("title", "Change Password");
        return "change-password";
    }
}
