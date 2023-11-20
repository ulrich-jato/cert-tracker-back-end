package com.devops.certtracker.repository;

import com.devops.certtracker.entity.RefreshToken;
import com.devops.certtracker.entity.User;
import com.devops.certtracker.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
