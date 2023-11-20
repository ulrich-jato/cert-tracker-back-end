package com.devops.certtracker.scheduler;

import com.devops.certtracker.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailScheduler {
    @Autowired
    private EmailService emailService;

    @Value("${application.email.notification.days}")
    private int days;


    // Execute every day at 8 AM in the "America/New_York" time zone
    @Scheduled(cron = "0 0 8 * * ?", zone = "America/New_York")
    public void sendEmail() {
        emailService.checkCertificateExpirationAndSendEmail(days);
    }
}
