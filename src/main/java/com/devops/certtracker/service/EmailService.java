package com.devops.certtracker.service;

import com.devops.certtracker.entity.Certificate;
import com.devops.certtracker.entity.EmailInformation;
import com.devops.certtracker.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}") private String from;

    @Autowired
    private  CertificateService certificateService;

    public void sendCertificateExpiryEmail(User user, String certUrlsEnglish, String certUrlsFrench)
            throws MessagingException, UnsupportedEncodingException {
        String subject = "Certificate Expiry Notification / Avis d'Expiration de Certificat";
        String senderName = "Certificate Tracker Service / Service de Suivi des Certificats";
        String mailContent = "<html><body>" +
                "<p>La version française de ce message suit.</p>" +
                "<p>Hi " + user.getFirstname() + ",</p>" +
                "<p><b>The following certificates are expiring, or have expired: </b></p>" +
                "<p><ul>" + certUrlsEnglish + "</ul></p>" +
                "<p>Thank you for choosing our service.</p>" +
                "<p>Best Regards,</p>" +
                "<p>Certificate Tracker Service (c)<br>cert.tracker.app@gmail.com</p>" +
                "<hr><br>" +

                "<p>Salut " + user.getFirstname() + ",</p>" +
                "<p><b>Les certificats suivants arrivent à expiration ou ont déjà expiré : </b></p>"  +
                "<p><ul>" + certUrlsFrench + "</ul></p>" +
                "<p>Nous vous remercions d'avoir choisi notre service.</p>" +
                "<p>Cordialement,</p>" +
                "<p>Service de Suivi des Certificats (c)<br>cert.tracker.app@gmail.com</p>" +
                "</body></html>";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("cert.tracker.app@gmail.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
    }


    public void sendVerificationEmail(User user, String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification / Vérification de courriel";
        String senderName = "Certificate Tracker Service / Service de Suivi des Certificats";
        String mailContent = "<html><body>" +
                "<p>La version française de ce message suit.</p>" +
                "<p>Hi " + user.getFirstname() + ",</p>" +
                "<p>Thank you for choosing our Ceryificate Tracker Service. Your registration is almost complete, but there is one important step left to take.</p>" +
                "<p>Please, follow the link below to verify your email address and activate your account:</p>" +
                "<p><a href=\"" + url + "\">Click here to verify your email address</a></p>" +
                "<p>This link will expire in 10 minutes, so make sure to complete the verification process promptly.</p>" +
                "<p>If the link does not work, please copy and paste the following URL into your web browser:</p>" +
                "<p>" + url + "</p>" +
                "<p>Thank you for choosing our service.</p>" +
                "<p>Best Regards,</p>" +
                "<p>Certificate Tracker Service (c)<br>cert.tracker.app@gmail.com</p>" +
                "<hr><br>" +

                "<p>Salut " + user.getFirstname() + ",</p>" +
                "<p>Nous tenons à vous remercier d'avoir choisi notre Service de Suivi des Certificats. Votre inscription est presque terminée, mais il reste une étape importante à accomplir.</p>" +
                "<p>Veuillez suivre le lien ci-dessous pour vérifier votre adresse électronique et activer votre compte:</p>" +
                "<p><a href=\"" + url + "\">Cliquez ici pour vérifier votre adresse électronique</a></p>" +
                "<p>Ce lien expirera dans 10 minutes, veuillez donc vous assurer de compléter le processus de vérification rapidement.</p>" +
                "<p>Si le lien ne fonctionne pas, veuillez copier et coller l'URL suivante dans votre navigateur web:</p>" +
                "<p>" + url + "</p>" +
                "<p>Nous vous remercions d'avoir choisi notre service.</p>" +
                "<p>Cordialement,</p>" +
                "<p>Service de Suivi des Certificats (c)<br>cert.tracker.app@gmail.com</p>" +
                "</body></html>";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("cert.tracker.app@gmail.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
    }

    public void sendPasswordResetVerificationEmail(User user, String code)
            throws MessagingException, UnsupportedEncodingException {
        String subject = "Password Reset Request / Réinitialisation du mot de passe";
        String senderName = "Certificate Tracker Service / Service de Suivi des Certificats";
        String mailContent = "<html><body>" +
                "<p>La version française de ce message suit.</p>" +
                "<p>Hi " + user.getFirstname() + ",</p>" +
                "<p><b>You recently requested to reset your password,</b></p>" +
                "<p>Please, use the following code to complete the action:</p>" +
                "<p><strong>Your code is: " + code + "</strong></p>" +
                "<p>This code will expire in 10 minutes, so make sure to complete the verification process promptly.</p>" +
                "<p>Thank you for choosing our service.</p>" +
                "<p>Best Regards,</p>" +
                "<p>Certificate Tracker Service (c)<br>cert.tracker.app@gmail.com</p>" +
                "<hr><br>" +

                "<p>Salut " + user.getFirstname() + ",</p>" +
                "<p><b>Vous avez récemment demandé la réinitialisation de votre mot de passe,</b></p>" +
                "<p>Veuillez utiliser le code suivant pour compléter l'action:</p>" +
                "<p><strong>Votre code est: " + code + "</strong></p>" +
                "<p>Ce code expirera dans 10 minutes, veuillez donc vous assurer de compléter le processus de vérification rapidement.</p>" +
                "<p>Nous vous remercions d'avoir choisi notre service.</p>" +
                "<p>Cordialement,</p>" +
                "<p>Service de Suivi des Certificats (c)<br>cert.tracker.app@gmail.com</p>" +
                "</body></html>";


        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("cert.tracker.app@gmail.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);
    }

    public void checkCertificateExpirationAndSendEmail(int days){
        List<Certificate> certificates = certificateService.getAllCertificates();
        Map<User, List<Certificate>> certificateByUser = certificates.stream()
                .collect(Collectors.groupingBy(Certificate::getUser));

        certificateByUser.forEach((user, userCertificates) -> {
            StringBuilder messageEnglish = new StringBuilder();
            StringBuilder messageFrench = new StringBuilder();
            for(Certificate certificate : userCertificates){
                Date currentDate = new Date();
                Date expirationDate = certificate.getValidTo();
                // Calculate the number of days until expiry
                long daysUntilExpiry = (expirationDate.getTime() - currentDate.getTime())/(24 * 60 * 60 * 1000);
                if(daysUntilExpiry <= 0 ){
                    messageEnglish.append("<li>").append(certificate.getUrl()).append(" is expired.").append("</li>");
                    messageFrench.append("<li>").append(certificate.getUrl()).append(" a expiré.").append("</li>");
                }else if(daysUntilExpiry <= days){
                    messageEnglish.append("<li>").append(certificate.getUrl()).append(" will expire in ")
                            .append("<b>").append(daysUntilExpiry).append("</b> days. </li>");
                    messageFrench.append("<li>").append(certificate.getUrl()).append(" expirera dans ")
                            .append("<b>").append(daysUntilExpiry).append("</b> jours. </li>");
                }
            }
            if(messageFrench.length() > 0 || messageEnglish.length() > 0){
                try {
                    sendCertificateExpiryEmail(user, messageEnglish.toString(), messageFrench.toString());
                } catch (MessagingException | UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }

        });

    }

}
