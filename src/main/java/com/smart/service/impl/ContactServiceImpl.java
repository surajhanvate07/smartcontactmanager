package com.smart.service.impl;

import com.smart.dao.ContactRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Override
    public Page<Contact> findContactsByUser(int userId, Pageable pageable) {
        return contactRepository.findContactsByUser(userId, pageable);
    }

    @Override
    public Optional<Contact> getContactById(int cId) {
        return contactRepository.findById(cId);
    }

    @Override
    public void deleteContact(Contact contact) {
        contactRepository.delete(contact);
    }

    @Override
    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public List<Contact> fetchContacts(String query, User user) {
        return contactRepository.findByNameContainingAndUser(query, user);
    }
}
