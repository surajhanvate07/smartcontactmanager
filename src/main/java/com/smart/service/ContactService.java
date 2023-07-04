package com.smart.service;

import com.smart.entities.Contact;
import com.smart.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ContactService {
    public Page<Contact> findContactsByUser(int userId, Pageable pageable);

    public Optional<Contact> getContactById(int cId);

    public void deleteContact(Contact contact);

    public Contact saveContact(Contact contact);

    public List<Contact> fetchContacts(String query, User user);
}
