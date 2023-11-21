package com.devops.certtracker.controller;

import com.devops.certtracker.dto.request.ChangePasswordRequest;
import com.devops.certtracker.dto.response.MessageResponse;
import com.devops.certtracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
//@CrossOrigin(origins = "*", maxAge = 3600)
//@CrossOrigin(origins = "http://127.0.0.1:8090", allowCredentials = "true")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userservice;
    @PatchMapping("/change-password")
    ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Principal authenticatedUser){
        userservice.changePassword(request, authenticatedUser);
        return  ResponseEntity.ok().body(new MessageResponse("Password changed successfully!"));
    }

}
