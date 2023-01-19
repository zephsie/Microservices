package com.zephsie.wellbeing.listeners;

import com.zephsie.wellbeing.events.OnRegistrationCompleteEvent;
import com.zephsie.wellbeing.utils.email.api.IEmailConfirmationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final IEmailConfirmationService emailConfirmationService;

    @Autowired
    public RegistrationListener(IEmailConfirmationService emailConfirmationService) {
        this.emailConfirmationService = emailConfirmationService;
    }

    @Override
    public void onApplicationEvent(@NonNull OnRegistrationCompleteEvent event) {
        try {
            emailConfirmationService.sendConfirmationEmail(event.getVerificationToken());
        } catch (Exception e) {
            log.error("Error sending confirmation email", e);
        }
    }
}