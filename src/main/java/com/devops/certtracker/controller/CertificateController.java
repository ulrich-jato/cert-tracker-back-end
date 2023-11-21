package com.devops.certtracker.controller;

import com.devops.certtracker.entity.Certificate;
import com.devops.certtracker.service.CertificateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin(origins = "http://127.0.0.1:8090", maxAge = 3600, allowCredentials = "true")
@RequestMapping("/api/certificates")
@Slf4j
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    @PostMapping("/info")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> getCertificateInfo(@RequestBody Map<String, String> requestBody) {
        String url = requestBody.get("url");
        Certificate certificate = certificateService.getCertificateInfo(url);
        return ResponseEntity.ok(certificate);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> addCertificate(@RequestBody Map<String, String> requestBody) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Authenticated user: {}", authentication.getName());
        log.debug("User roles: {}", authentication.getAuthorities());
        String url = requestBody.get("url");
        Certificate certificate = certificateService.retrieveAndSaveCertificate(url);
        return ResponseEntity.ok(certificate);
    }

    @DeleteMapping("delete/{certificateId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCertificateById(@PathVariable Long certificateId){
        certificateService.deleteCertificateById(certificateId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("delete/user/{certificateId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserCertificateById(@PathVariable Long certificateId){
        certificateService.deleteUserCertificateById(certificateId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    ResponseEntity<List<Certificate>> getAllCertificates(){
        List<Certificate> certificates = certificateService.getAllCertificates();
        return ResponseEntity.ok(certificates);
    }
    @GetMapping("/user/all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    ResponseEntity<List<Certificate>> getAllUserCertificates(){
        List<Certificate> certificates = certificateService.getAllUserCertificates();
        return ResponseEntity.ok(certificates);
    }


    @GetMapping("get/{certificateId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    ResponseEntity<Object> getCertificateById(@PathVariable Long certificateId){
        Certificate certificate = certificateService.getCertificateById(certificateId);
        return ResponseEntity.ok(certificate);
    }

}
