package com.devops.certtracker.service;

import com.devops.certtracker.entity.User;
import com.devops.certtracker.entity.VerificationToken;
import com.devops.certtracker.repository.UserRepository;
import com.devops.certtracker.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class VerificationTokenService {
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Value("${application.jwt.verification.expiration}")
    private Long verificationTokenExpiration;
    public VerificationToken createVerificationToken(User user){
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setExpirationDate(Instant.now().plusMillis(verificationTokenExpiration));
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken = verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setExpirationDate(Instant.now().plusMillis(verificationTokenExpiration));
        verificationToken = verificationTokenRepository.save(verificationToken);
        return  verificationToken;
    }

    public String validateToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return "Invalid verification token";
        }
        User user = verificationToken.getUser();

        if (verificationToken.getExpirationDate().compareTo(Instant.now()) <= 0) {
            //verificationTokenRepository.delete(verificationToken);
            return "The verification link has already expired. "+
                    "Please click the link below to request a new verification link.";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }


}
