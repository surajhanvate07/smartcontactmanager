package com.smart.controller;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.ContactService;
import com.smart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();

        User user = userService.getUserByUserName(userName);

        model.addAttribute("user", user);
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String userDashboard(Model model, HttpSession session) {
        model.addAttribute("title", "User's Dashboard");
        session.setAttribute("message", new Message("Logged in Successfully", "alert-success"));
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

            User loggedUser = userService.getUserByUserName(name);

            // Processing and uploading file...
            if (file.isEmpty()) {
                contact.setImage("contact.png");
            } else {
                // upload the file to folder and update the name to contact
                contact.setImage(file.getOriginalFilename());

                File uploadFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(uploadFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            contact.setUser(loggedUser);
            loggedUser.getContacts().add(contact);

            userService.addUser(loggedUser);

            session.setAttribute("message", new Message("Your contact is added..!", "alert-success"));
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong, Try again", "alert-danger"));
        }
        return "normal/add-contact-form";
    }

    @RequestMapping(value = "/show-contacts/{page}", method = RequestMethod.GET)
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
        String userName = principal.getName();
        User loggedUser = userService.getUserByUserName(userName);

        //Contact Page - page
        //Contact per Page - 5
        Pageable pageable = PageRequest.of(page, 5);

        Page<Contact> contactList = contactService.findContactsByUser(loggedUser.getId(), pageable);

        model.addAttribute("title", "Show User Contacts");
        model.addAttribute("contacts", contactList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contactList.getTotalPages());

        return "normal/show-contacts";
    }

    @RequestMapping(value = "/{cId}/contact", method = RequestMethod.GET)
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {

        Optional<Contact> optionalContact = contactService.getContactById(cId);
        Contact contact = optionalContact.get();

        String userName = principal.getName();
        User user = userService.getUserByUserName(userName);

        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());
        } else {
            model.addAttribute("title", "Contact Detail");
        }

        return "normal/contact-detail";
    }

    @RequestMapping(value = "/delete/{cId}", method = RequestMethod.GET)
    public String deleteContact(@PathVariable("cId") Integer cId, HttpSession session) {
        Contact contact = contactService.getContactById(cId).get();

        contactService.deleteContact(contact);

        session.setAttribute("message", new Message("Contact deleted successfully..!", "alert-success"));

        return "redirect:/user/show-contacts/0";
    }

    @RequestMapping(value = "/update-contact/{cId}", method = RequestMethod.POST)
    public String updateContact(@PathVariable("cId") Integer cId, Model model) {
        Contact contact = contactService.getContactById(cId).get();

        model.addAttribute("title", "Update Contact");
        model.addAttribute("contact", contact);
        return "normal/update-contact";
    }

    @RequestMapping(value = "/process-update", method = RequestMethod.POST)
    public String updateContactHandler(@ModelAttribute("contact") Contact contact, @RequestParam("profileImage") MultipartFile file, Model model, HttpSession session, Principal principal) {
        try {
            //Getting the old contact
            Contact oldContactDetail = contactService.getContactById(contact.getcId()).get();

            if (!file.isEmpty()) {
                //delete the old image
                File deleteFile = new ClassPathResource("static/img").getFile();
                File dFile = new File(deleteFile, oldContactDetail.getImage());
                dFile.delete();

                // update the new image
                File uploadFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(uploadFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());
            } else {
                contact.setImage(oldContactDetail.getImage());
            }
            String userName = principal.getName();
            User user = userService.getUserByUserName(userName);
            contact.setUser(user);

            contactService.saveContact(contact);
            session.setAttribute("message", new Message("Contact Updated Successfully..!", "alert-success"));
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Oops, Something went wrong!", "alert-danger"));
        }

        return "redirect:/user/" + contact.getcId() + "/contact";
    }

    @RequestMapping(value = "/show-profile/{id}", method = RequestMethod.GET)
    public String showProfile(@PathVariable("id") int id, Model model) {
        User loggedUser = userService.getUserById(id).get();
        model.addAttribute("user", loggedUser);
        model.addAttribute("title", "User Profile Page");
        return "normal/show-profile";
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String showSettings(Model model) {
        model.addAttribute("title", "User Settings");
        return "normal/settings";
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
        User loggedUser = userService.getUserByUserName(principal.getName());

        if(bCryptPasswordEncoder.matches(oldPassword, loggedUser.getPassword())) {
            loggedUser.setPassword(bCryptPasswordEncoder.encode(newPassword));

            userService.saveUser(loggedUser);

            session.setAttribute("message", new Message("Password Changed Successfully.!!","alert-success"));
            return "redirect:/user/dashboard";
        }
        else {
            session.setAttribute("message", new Message("Old Password doesn't matches, Please try again.!!","alert-danger"));
            return "redirect:/user/settings";
        }
    }
}
