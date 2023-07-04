package com.smart.controller;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.service.ContactService;
import com.smart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private UserService userService;
    @Autowired
    private ContactService contactService;

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {
        User user = userService.getUserByUserName(principal.getName());
        List<Contact> contacts = contactService.fetchContacts(query, user);
        return ResponseEntity.ok(contacts);
    }
}
