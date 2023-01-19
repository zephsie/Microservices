package com.zephsie.wellbeing.utils.email.api;

import com.zephsie.wellbeing.models.entity.VerificationToken;

public interface IEmailConfirmationService {
    void sendConfirmationEmail(VerificationToken token);
}