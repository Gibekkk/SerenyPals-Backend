package com.serenypals.restfulapi.service;

import java.util.Optional;
import java.util.Random;
import java.util.Map;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.model.OTP;
import com.serenypals.restfulapi.repository.OTPRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

import com.serenypals.restfulapi.dto.EmailDetails;
import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public void sendEmail(EmailDetails details) {
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            // Sending the mail
            javaMailSender.send(mailMessage);
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}