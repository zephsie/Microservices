package com.zephsie.wellbeing.utils.email.entity;

import com.zephsie.wellbeing.models.entity.VerificationToken;
import com.zephsie.wellbeing.utils.email.api.IEmailConfirmationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailConfirmationService implements IEmailConfirmationService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailConfirmationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendConfirmationEmail(VerificationToken verificationToken) {
        String recipientAddress = verificationToken.getUser().getEmail();
        String subject = "Registration Confirmation";
        String message = "Your verification code is " + verificationToken.getToken();

        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }
}
