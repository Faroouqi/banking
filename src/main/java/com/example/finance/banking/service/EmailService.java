package com.example.finance.banking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    public void sendVerificationEmail(String toEmail, String verificationCode) {
        try{
            String subject = "🔐 Verify Your Email - Finance Manager App";

            String message = "Hello,\n\n"
                    + "Welcome to *Finance Manager*! 🎉\n\n"
                    + "To complete your registration and start managing your finances securely, "
                    + "please verify your email using the one-time verification code below:\n\n"
                    + "👉 Verification Code: " + verificationCode + "\n\n"
                    + "This code will expire in 10 minutes for your security.\n\n"
                    + "If you did not sign up for Finance Manager, please ignore this email.\n\n"
                    + "Best regards,\n"
                    + "The Finance Manager Team";

            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(toEmail);
            email.setSubject(subject);
            email.setText(message);

            javaMailSender.send(email);
        }catch (Exception e)
        {
            log.error("Exception occurred during mail sender", e);

        }
    }

}
