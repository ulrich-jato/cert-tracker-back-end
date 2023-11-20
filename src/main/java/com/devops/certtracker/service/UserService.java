package com.devops.certtracker.service;

import com.devops.certtracker.dto.request.ChangePasswordRequest;
import com.devops.certtracker.entity.User;
import com.devops.certtracker.exception.ChangePasswordException;
import com.devops.certtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public  void changePassword(ChangePasswordRequest request, Principal authenticatedUser){
        //authenticatedUser = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //UserDetailsImpl user = (UserDetailsImpl) authenticatedUser;
        UserDetailsImpl userDetails = (UserDetailsImpl)((UsernamePasswordAuthenticationToken) authenticatedUser).getPrincipal();

        // check if the current password is correct
       if (!passwordEncoder.matches(request.getCurrentPassword(), userDetails.getPassword())){
           throw new ChangePasswordException("Wrong password provided");
       }

       // check if the two new passwords are the same
       if(!request.getNewPassword().equals(request.getConfirmationPassword())){
           throw new ChangePasswordException("Provided passwords do not match");
       }

       // retrieve the user based on the email from userDetails
       User user = userRepository.findByEmail(userDetails.getUsername())
                       .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

       //Update the password
       user.setPassword(passwordEncoder.encode(request.getNewPassword()));

       // Save the password
       userRepository.save(user);
    }

    public void changePassword(User theUser, String newPassword) {
        theUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(theUser);
    }
}
