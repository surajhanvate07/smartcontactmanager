package com.smart.controller;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

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

    @RequestMapping(value = "/process-contact", method = RequestMethod.POST)
    public String processContact(@ModelAttribute("contact") Contact contact, @RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session) {
        try {
            String name = principal.getName();

            User loggedUser = userRepository.getUserByUserName(name);

            // Processing and uploading file...
            if (file.isEmpty()) {

            } else {
                // upload the file to folder and update the name to contact
                contact.setImage(file.getOriginalFilename());

                File uploadFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(uploadFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            contact.setUser(loggedUser);
            loggedUser.getContacts().add(contact);

            userRepository.save(loggedUser);

//            System.out.println("CONTACT :" + contact);
            session.setAttribute("message", new Message("Your contact is added..!", "alert-success"));
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong, Try again", "alert-danger"));
        }
        return "normal/add-contact-form";
    }

    @RequestMapping(value = "/show-contacts", method = RequestMethod.GET)
    public String showContacts(Model model, Principal principal) {

        String userName = principal.getName();
        User loggedUser = userRepository.getUserByUserName(userName);

        List<Contact> contactList = contactRepository.findContactsByUser(loggedUser.getId());

        System.out.println(contactList);

        model.addAttribute("contacts", contactList);
        model.addAttribute("title", "Show User Contacts");

        return "normal/show-contacts";
    }
}
