/**
 * This package contains the service classes for managing certificates.
 */
package com.devops.certtracker.service;

import com.devops.certtracker.entity.Certificate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for the EmailService. It contains test cases for various
 * operations related to emails.
 */
@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    // Mocked repository for simulating interactions with the Certificate database.

    @Mock
    private CertificateService certificateService;

    @Mock
    private JavaMailSender javaMailSender;
    // The service under test, which will be automatically injected with mocked dependencies.
    @InjectMocks
    private EmailService emailService;

    // Sample Certificate instances used for testing purposes.
    private Certificate certificate1, certificate2;

    private Date tenDaysFromNow, tenDaysAgo, twoMonthsFromNow;

    private SimpleMailMessage message;

    private String body, recipient;

    /**
     * Initialize test data before each test case.
     */
    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        tenDaysFromNow = new Date(System.currentTimeMillis() + (long) 10 * 24 * 60 * 60 * 1000);
        tenDaysAgo = new Date(System.currentTimeMillis() - (long) 10 * 24 * 60 * 60 * 1000);
        twoMonthsFromNow = new Date(System.currentTimeMillis() + (long) 60 * 24 * 60 * 60 * 1000);
        certificate1 = new Certificate();
        certificate1.setUrl("https://www.google.com");
        certificate1.setSubject("CN=google.com");
        certificate1.setIssuer("CN=issuer.com");
        certificate1.setValidFrom(tenDaysAgo);
        certificate1.setValidTo(tenDaysFromNow);

        certificate2 = new Certificate();
        certificate2.setUrl("https://www.github.com");
        certificate2.setSubject("CN=github.com");
        certificate2.setIssuer("CN=issuer.com");
        certificate2.setValidFrom(tenDaysAgo);
        certificate2.setValidTo(twoMonthsFromNow);

        body = "The following certificates are expiring, or have expired, within the next 14:\n" +
                "https://www.google.com - "+tenDaysFromNow.toString().substring(0,10)+"\n";

        recipient = "kyle.ryc@gmail.com";
        message = new SimpleMailMessage();
        message.setFrom("wildryc.tester@gmail.com");
        message.setSubject("Certificate Expiry Notification");
        message.setText(body);
        message.setTo(recipient);

        // user a mock bean to get a SpringBoot application context

    }

    /**
     * Test getting a list of certificates that are expiring in the next 14 days.
     */
    @Test
    @DisplayName("Get list of certificates expiring in the next 14 days")
    public void testWriteEmailBody() {

    }

    /**
     * Test sending an email to the user.
     */
    @Test
    @DisplayName("Send email to user")
    public void testSendEmail() {

    }
}
