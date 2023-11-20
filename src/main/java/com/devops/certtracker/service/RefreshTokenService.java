package com.devops.certtracker.service;

import com.devops.certtracker.entity.RefreshToken;
import com.devops.certtracker.entity.User;
import com.devops.certtracker.exception.RefreshTokenException;
import com.devops.certtracker.repository.RefreshTokenRepository;
import com.devops.certtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${application.security.jwt.refresh.expiration}")
    private Long refreshTokenExpiration;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public Optional<RefreshToken> deleteByToken(String token){
        return refreshTokenRepository.deleteByToken(token);
    }

//    public Set<RefreshToken> getRefreshTokens(Long userId) {
//        User user = userRepository.findById(userId).orElse(null);
//        if (user != null) {
//            return user.getRefreshTokens();
//        }
//        return new HashSet<>();
//    }

    public RefreshToken createRefreshToken(Long userId){
        User user = userRepository.findById(userId).orElse(null);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpirationDate(Instant.now().plusMillis(refreshTokenExpiration));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        //user.getRefreshTokens().add(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpirationDate().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getToken(), "Refresh token is expired. Please make a new sign-in request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId){
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
