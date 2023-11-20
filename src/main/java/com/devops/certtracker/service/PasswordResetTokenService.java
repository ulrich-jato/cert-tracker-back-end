package com.devops.certtracker.service;

import com.devops.certtracker.entity.User;
import com.devops.certtracker.entity.PasswordResetToken;
import com.devops.certtracker.repository.UserRepository;
import com.devops.certtracker.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Value("${application.jwt.verification.expiration}")
    private Long passwordResetTokenExpiration;
    @Value("${application.code.length}")
    private int codeSize;
    public PasswordResetToken createPasswordResetToken(User user){
        // Clear existing password reset tokens for the user
        clearExistingToken(user);
        // create a new access token
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        passwordResetToken.setExpirationDate(Instant.now().plusMillis(passwordResetTokenExpiration));
        passwordResetToken.setToken(generateSecureRandomCode(codeSize));
        passwordResetToken = passwordResetTokenRepository.save(passwordResetToken);
        return passwordResetToken;
    }

    public void clearExistingToken(User user){
        // Find and delete existing password reset tokens for the user
        List<PasswordResetToken> existingTokens = passwordResetTokenRepository.findByUser(user);
        passwordResetTokenRepository.deleteAll();
    }

    private String generateSecureRandomCode(int length) {
        // Define characters to be used for the token (0-9)
        String characters = "0123456789";
        StringBuilder token = new StringBuilder();

        // Generate random token of the specified length using SecureRandom
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(characters.length());
            token.append(characters.charAt(index));
        }
        return token.toString();
    }


    public String validateToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null) {
            return "Invalid password reset token";
        }
        if (passwordResetToken.getExpirationDate().compareTo(Instant.now()) <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "The code has expired. Please send a new request.";

        }
        return "valid";
    }

    public Optional<User> findUserByPasswordToken(String passwordResetToken) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(passwordResetToken).getUser());
    }

    public PasswordResetToken findPasswordResetToken(String token){
        return passwordResetTokenRepository.findByToken(token);
    }

}
