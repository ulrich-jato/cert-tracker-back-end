package com.devops.certtracker.controller;

import com.devops.certtracker.dto.request.ResetPasswordRequest;
import com.devops.certtracker.dto.request.SigninRequest;
import com.devops.certtracker.dto.request.SignupRequest;
import com.devops.certtracker.dto.response.RefreshTokenResponse;
import com.devops.certtracker.dto.response.SigninResponse;
import com.devops.certtracker.dto.response.MessageResponse;
import com.devops.certtracker.dto.response.SignoutResponse;
import com.devops.certtracker.entity.ErrorResponse;
import com.devops.certtracker.service.AuthenticationService;
import com.devops.certtracker.service.PasswordResetTokenService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest signupRequest, final HttpServletRequest request){
        MessageResponse response = authenticationService.register(signupRequest, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@Valid @RequestBody SigninRequest signinRequest){
        SigninResponse authenticationResponse = authenticationService.authenticate(signinRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authenticationResponse.getJwtCookie())
                .header(HttpHeaders.SET_COOKIE, authenticationResponse.getJwtRefreshCookie())
                .body(authenticationResponse.getUserInfoResponse());
    }
    @PostMapping("/signout")
    public ResponseEntity<?> signout(){
        SignoutResponse response =  authenticationService.signout();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,response.getJwtCookie())
                .header(HttpHeaders.SET_COOKIE,response.getJwtRefreshCookie())
                .body(response.getMessageResponse());
    }
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request){
        RefreshTokenResponse response =  authenticationService.refreshToken(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.getJwtCookie())
                .body(response.getMessageResponse());
    }
    @PostMapping("/password-reset-request")
    public  ResponseEntity<MessageResponse> resetPasswordRequest(
            @RequestBody ResetPasswordRequest resetPasswordRequest,
            final HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        //String response = authenticationService.resetPasswordRequest(resetPasswordRequest, request);
        authenticationService.resetPasswordRequest(resetPasswordRequest, request);
        return ResponseEntity.ok(new MessageResponse("For your security, we've sent you an email with password reset instructions"));
    }
    @PostMapping("/password-reset")
    public  ResponseEntity<MessageResponse> resetPassword(
            @RequestBody ResetPasswordRequest resetPasswordRequest,
            @RequestParam("token") String token){
        MessageResponse response = authenticationService.resetPassword(resetPasswordRequest, token);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/verifyEmail")
    public ResponseEntity<String> sendVerificationToken(@RequestParam("token") String token){
        String response =   authenticationService.sendVerificationToken(token);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/resend-verification-token")
    public ResponseEntity<String> resendVerificationToken(@RequestParam("token") String oldToken, final HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException {
        String response = authenticationService.resendVerificationToken(oldToken, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate-password-code")
    public ResponseEntity<?> validatePasswordResetCode(@RequestParam("code") String code){
       MessageResponse response= authenticationService.validatePasswordResetCode(code);
       return ResponseEntity.ok(response);
    }
}
