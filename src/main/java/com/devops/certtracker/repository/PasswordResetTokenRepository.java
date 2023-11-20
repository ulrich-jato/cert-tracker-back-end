package com.devops.certtracker.repository;

import com.devops.certtracker.entity.PasswordResetToken;
import com.devops.certtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String passwordResetToken);

    List<PasswordResetToken> findByUser(User user);

}
