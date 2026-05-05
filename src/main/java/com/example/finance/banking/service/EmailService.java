package com.example.finance.banking.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendVerificationEmail(String toEmail, String verificationCode) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Verify Your Email - Finance Manager");
            helper.setText(buildEmailHtml(verificationCode), true); // true = isHtml

            javaMailSender.send(mimeMessage);
            log.info("Verification email sent successfully to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}", toEmail, e);
        }
    }

    private String buildEmailHtml(String verificationCode) {
        // Build individual styled digit boxes for the OTP code
        String[] digits = verificationCode.split("");
        StringBuilder codeBoxes = new StringBuilder();
        for (String digit : digits) {
            codeBoxes.append(
                    "<td style='padding:0 5px;'>" +
                            "<div style='" +
                            "display:inline-block;" +
                            "width:48px;height:56px;line-height:56px;" +
                            "text-align:center;" +
                            "font-size:28px;font-weight:700;" +
                            "color:#1a1a2e;" +
                            "background:#f0f4ff;" +
                            "border:2px solid #c7d4f7;" +
                            "border-radius:10px;" +
                            "'>" + digit + "</div>" +
                            "</td>"
            );
        }

        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'/>" +
                "<meta name='viewport' content='width=device-width,initial-scale=1.0'/>" +
                "<title>Verify Your Email</title>" +
                "</head>" +
                "<body style='margin:0;padding:0;background:#eef2ff;" +
                "font-family:Segoe UI,Helvetica,Arial,sans-serif;'>" +

                "<table width='100%' cellpadding='0' cellspacing='0' style='background:#eef2ff;padding:40px 0;'>" +
                "<tr><td align='center'>" +

                "<table width='580' cellpadding='0' cellspacing='0' style='" +
                "background:#ffffff;border-radius:20px;overflow:hidden;" +
                "box-shadow:0 8px 40px rgba(99,102,241,0.12);max-width:580px;'>" +

                // Header Banner
                "<tr>" +
                "<td style='background:linear-gradient(135deg,#4f46e5 0%,#7c3aed 100%);" +
                "padding:40px 48px 36px;text-align:center;'>" +
                "<div style='display:inline-block;background:rgba(255,255,255,0.15);" +
                "border-radius:16px;padding:12px 18px;margin-bottom:16px;'>" +
                "<span style='font-size:28px;'>&#128176;</span>" +
                "</div>" +
                "<h1 style='margin:0;color:#ffffff;font-size:26px;font-weight:700;" +
                "letter-spacing:-0.5px;'>Finance Manager</h1>" +
                "<p style='margin:6px 0 0;color:rgba(255,255,255,0.75);font-size:14px;'>" +
                "Secure. Simple. Smart.</p>" +
                "</td>" +
                "</tr>" +

                // Body
                "<tr>" +
                "<td style='padding:44px 48px 16px;'>" +
                "<h2 style='margin:0 0 12px;color:#1a1a2e;font-size:22px;font-weight:700;'>" +
                "Verify your email address</h2>" +
                "<p style='margin:0 0 28px;color:#4b5563;font-size:15px;line-height:1.7;'>" +
                "Hi there! Welcome aboard. Use the code below to confirm your email " +
                "and activate your Finance Manager account.</p>" +

                "<table cellpadding='0' cellspacing='0' width='100%' style='" +
                "background:#f8faff;border:1.5px solid #e0e7ff;" +
                "border-radius:14px;margin-bottom:28px;'>" +
                "<tr><td style='padding:28px;text-align:center;'>" +
                "<p style='margin:0 0 16px;color:#6b7280;font-size:13px;" +
                "font-weight:600;letter-spacing:1px;text-transform:uppercase;'>" +
                "Your One-Time Code</p>" +
                "<table cellpadding='0' cellspacing='0' align='center'>" +
                "<tr>" + codeBoxes + "</tr>" +
                "</table>" +
                "<p style='margin:18px 0 0;color:#9ca3af;font-size:12px;'>" +
                "Expires in <strong style='color:#4f46e5;'>10 minutes</strong></p>" +
                "</td></tr>" +
                "</table>" +

                "<table cellpadding='0' cellspacing='0' width='100%' style='" +
                "background:#fefce8;border-left:4px solid #f59e0b;" +
                "border-radius:0 8px 8px 0;margin-bottom:32px;'>" +
                "<tr><td style='padding:14px 18px;'>" +
                "<p style='margin:0;color:#92400e;font-size:13px;line-height:1.6;'>" +
                "&#128274; <strong>Security tip:</strong> Never share this code with anyone. " +
                "Finance Manager will never ask for it via phone or chat.</p>" +
                "</td></tr>" +
                "</table>" +

                "<p style='margin:0 0 8px;color:#6b7280;font-size:14px;line-height:1.6;'>" +
                "Didn't create an account? Safely ignore this email &#8212; no action needed.</p>" +
                "</td>" +
                "</tr>" +

                "<tr><td style='padding:0 48px;'>" +
                "<hr style='border:none;border-top:1px solid #f3f4f6;margin:0;'/>" +
                "</td></tr>" +

                "<tr>" +
                "<td style='padding:24px 48px 36px;text-align:center;'>" +
                "<p style='margin:0 0 4px;color:#9ca3af;font-size:12px;'>" +
                "&#169; 2025 Finance Manager &#183; All rights reserved</p>" +
                "<p style='margin:0;color:#9ca3af;font-size:12px;'>" +
                "Questions? <a href='mailto:support.financemanager@gmail.com' " +
                "style='color:#4f46e5;text-decoration:none;'>support.financemanager@gmail.com</a></p>" +
                "</td>" +
                "</tr>" +

                "</table>" +
                "</td></tr>" +
                "</table>" +
                "</body></html>";
    }
}