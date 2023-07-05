package com.smart.service;

public interface EmailService {
    public boolean sendEmail(String subject, String message, String to);
}
