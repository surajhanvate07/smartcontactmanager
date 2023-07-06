package com.smart.controller;

import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.EmailService;
import com.smart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class ForgotController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping(value = "/forgot", method = RequestMethod.GET)
    public String showForgotPassword(Model model) {
        model.addAttribute("title", "Forgot Password");
        return "forgot-password";
    }

    @RequestMapping(value = "/send-otp", method = RequestMethod.POST)
    public String sendOTPHandler(@RequestParam("username") String username, Model model, HttpSession session) {
        User loggedUser = userService.getUserByUserName(username);

        if (loggedUser == null) {
            model.addAttribute("error", "");
            session.setAttribute("message", new Message("Email Doesn't exists, enter a correct one", "alert-danger"));
            return "redirect:/forgot";
        }

        model.addAttribute("title", "OTP Verification");
        model.addAttribute("user", loggedUser);

        int min = 1000; // Minimum value of range
        int max = 9999; // Maximum value of range
        int otp = (int) Math.floor(Math.random() * (max - min + 1) + min);

        session.setAttribute("otp", otp);
        session.setAttribute("email", username);

        String subject = "OTP From Smart Contact Manage Application";
        String message = "<div style='border:1px solid #e2e2e; padding:10px'>"
                + "<h1>"
                + "Your OTP is = "
                + "<b>" + otp
                + "</h1"
                + "</div>";

        boolean emailFlag = emailService.sendEmail(subject, message, username);
        System.out.println(emailFlag);

        if (emailFlag) {
            session.setAttribute("message", new Message("OTP Sent Successfully!", "alert-success"));
            return "verify-otp";
        } else {
            session.setAttribute("message", new Message("OOPS, something went wrong, please try again!", "alert-danger"));
            return "redirect:/forgot";
        }
    }

    @RequestMapping(value = "/authorized-otp", method = RequestMethod.POST)
    public String changePasswordHandler(@RequestParam("first") String first, @RequestParam("second") String second, @RequestParam("third") String third, @RequestParam("fourth") int fourth, Model model, Principal principal, HttpSession session) {

        String userName = (String) session.getAttribute("email");

        User forgotUser = userService.getUserByUserName(userName);

        int otp = (int) session.getAttribute("otp");
        String received_otp = Integer.toString(otp);

        System.out.println("Received OTP :" + received_otp);

        String input_otp = first + second + third + fourth;
        System.out.println("Entered OTP :" + input_otp);
        boolean check_valid = input_otp.equalsIgnoreCase(received_otp);

        if (check_valid) {
            model.addAttribute("title", "Change Password");
            session.setAttribute("message", new Message("OTP Verified Successfully.!!", "alert-success"));
            return "change-password";
        } else {
            model.addAttribute("user", forgotUser);
            session.setAttribute("message", new Message("OOPS! You have entered wrong OTP, Please check again..!", "alert-danger"));
            return "verify-otp";
        }
    }

    @RequestMapping(value = "/process-change-password", method = RequestMethod.POST)
    public String passwordChangeHandler(@RequestParam("firstPass") String firstPass, @RequestParam("secondPass") String secondPass, HttpSession session) {
        boolean checkPasswords = firstPass.equals(secondPass);
        if (checkPasswords) {
            String username = (String) session.getAttribute("email");
            User forgotUser = userService.getUserByUserName(username);

            forgotUser.setPassword(bCryptPasswordEncoder.encode(firstPass));

            userService.saveUser(forgotUser);

            return "redirect:/signin?change=Password Changed Successfully..!";
        } else {
            session.setAttribute("message", new Message("Password doesn't matches, Please Check.!", "alert-danger"));
            return "change-password";
        }
    }

}
