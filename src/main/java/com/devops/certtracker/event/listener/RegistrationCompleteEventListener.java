package com.devops.certtracker.event.listener;

import com.devops.certtracker.entity.User;
import com.devops.certtracker.entity.VerificationToken;
import com.devops.certtracker.event.RegistrationCompleteEvent;
import com.devops.certtracker.service.EmailService;
import com.devops.certtracker.service.VerificationTokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    @Autowired
    private VerificationTokenService verificationTokenService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private EmailService emailService;
    private User user;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        user = event.getUser();
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(user);
        String token = verificationToken.getToken();
        String url = event.getApplicationUrl()+"/api/auth/verifyEmail?token="+token;
        try{
            emailService.sendVerificationEmail(user, url);
        }catch(MessagingException | UnsupportedEncodingException e){
            throw new RuntimeException(e);
        }
    }
}
