package com.devops.certtracker.repository;

import com.devops.certtracker.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findAllByUser_Id(Long userId);
}
