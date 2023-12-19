package com.devops.certtracker.service;

import com.devops.certtracker.dto.request.ResetPasswordRequest;
import com.devops.certtracker.dto.request.SigninRequest;
import com.devops.certtracker.dto.request.SignupRequest;
import com.devops.certtracker.dto.response.*;
import com.devops.certtracker.entity.*;
import com.devops.certtracker.event.RegistrationCompleteEvent;
import com.devops.certtracker.event.listener.RegistrationCompleteEventListener;
import com.devops.certtracker.exception.ChangePasswordException;
import com.devops.certtracker.exception.EmailAlreadyInUseException;
import com.devops.certtracker.exception.PasswordResetException;
import com.devops.certtracker.exception.RefreshTokenException;
import com.devops.certtracker.repository.RoleRepository;
import com.devops.certtracker.repository.UserRepository;
import com.devops.certtracker.repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private HttpServletRequest servletRequest;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private VerificationTokenService verificationTokenService;
    @Autowired
    private PasswordResetTokenService passwordResetTokenService;
    @Autowired
    private  UserService userService;
    @Autowired
    private RegistrationCompleteEventListener eventListener;
    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public SigninResponse authenticate(SigninRequest signinRequest) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            ResponseCookie jwtCookie = jwtService.generateJwtCookie(userDetails);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            ResponseCookie jwtRefreshCookie = jwtService.generateRefreshJwtCookie(refreshToken.getToken());

            return new SigninResponse(
                    jwtCookie.toString(),
                    jwtRefreshCookie.toString(),
                    new UserInfoResponse(userDetails.getId(),
                            userDetails.getFirstname(),
                            userDetails.getLastname(),
                            userDetails.getUsername(),
                            roles)
            );
        } catch (BadCredentialsException ex){
            throw new BadCredentialsException("Bad Credentials");
        }catch (DisabledException ex){
            throw new DisabledException("User account is disabled");
        }
    }
   public MessageResponse register(SignupRequest signupRequest, HttpServletRequest request) {
       if (userRepository.existsByEmail(signupRequest.getEmail())){
           throw new EmailAlreadyInUseException("Email is already in use !");
       }
       User user = createUserFromRequest(signupRequest);
       userRepository.save(user);
       //publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
       return new MessageResponse("User registered successfully! Check your email to complete your registration");
   }

    public String applicationUrl(HttpServletRequest request){
        return "http://"+ request.getServerName() + ":" + request.getServerPort() +
                request.getContextPath();
    }
    private User createUserFromRequest(SignupRequest signupRequest) {
        String encodedPassword = encoder.encode(signupRequest.getPassword());
        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(strRoles == null || strRoles.isEmpty()) {
            roles.add(getRole(ERole.ROLE_USER));
        }else {
            strRoles.forEach(role -> roles.add(getRole(mapRoleStringToEnum(role))));
        }
        User user = new User(signupRequest.getFirstname(),signupRequest.getLastname(),signupRequest.getEmail(),encodedPassword);
        user.setRoles(roles);
        return  user;
    }

    private Role getRole(ERole roleName) {
        return  roleRepository.findByName(roleName)
                .orElseThrow( () -> new RuntimeException("Error: Role is not found"));
    }

    private ERole mapRoleStringToEnum(String strRole) {
        return switch (strRole) {
            case "admin" -> ERole.ROLE_ADMIN;
            case "mod" -> ERole.ROLE_MODERATOR;
            default -> ERole.ROLE_USER;
        };
    }

    public String sendVerificationToken(String token){
        String url = applicationUrl(servletRequest)+"/api/auth/resend-verification-token?token="+token;

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return "Invalid verification token";
        }
        if(verificationToken.getUser().isEnabled()){
            return "This account has already been verified, please login";
        }
        String verificationResult = verificationTokenService.validateToken(token);
        if(verificationResult.equalsIgnoreCase("valid")){
            return "Email verified successfully. You can now login to your account";
        }
        return "Invalid verification link, <a href=\"" +url+"\"> Get a new verification link. </a>";
    }




    public String resendVerificationToken(String oldToken, HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException {
        VerificationToken verificationToken = verificationTokenService.generateNewVerificationToken(oldToken);
        User user = verificationToken.getUser();
        resendRegistrationVerificationTokenEmail(user, applicationUrl(request), verificationToken);
        return "A new verification link has been sent to your email. Please check your email to activate your account";
    }

    private void resendRegistrationVerificationTokenEmail(
            User user, String applicationUrl, VerificationToken verificationToken)
            throws MessagingException, UnsupportedEncodingException {
        String url = applicationUrl+"/api/auth/verifyEmail?token="+verificationToken.getToken();
        emailService.sendVerificationEmail(user,url);
    }


    public void resetPasswordRequest(ResetPasswordRequest resetPasswordRequest,
                                        HttpServletRequest servletRequest)
            throws MessagingException, UnsupportedEncodingException {

        Optional<User> user = userRepository.findByEmail(resetPasswordRequest.getEmail());
        String passwordResetUrl = "";
        if (user.isPresent()) {
            var passwordResetToken = passwordResetTokenService.createPasswordResetToken(user.get());
            emailService.sendPasswordResetVerificationEmail(user.get(), passwordResetToken.getToken());
        }
    }

    public MessageResponse resetPassword(ResetPasswordRequest resetPasswordRequest, String token){
        String tokenVerificationResult = passwordResetTokenService.validateToken(token);
        if (!tokenVerificationResult.equalsIgnoreCase("valid")) {
            throw new PasswordResetException(tokenVerificationResult);
        }
        Optional<User> user = passwordResetTokenService.findUserByPasswordToken(token);
        if (user.isPresent()) {
            userService.changePassword(user.get(), resetPasswordRequest.getNewPassword());
            passwordResetTokenService.clearExistingToken(user.get());
            return new MessageResponse("Password has been reset successfully");
        } else {
            throw new PasswordResetException("User not found for the given token");
        }
    }
    public MessageResponse validatePasswordResetCode(@RequestParam("code") String code){
        String tokenVerificationResult = passwordResetTokenService.validateToken(code);
        if (tokenVerificationResult.equalsIgnoreCase("valid")){
            return new MessageResponse("The code valid");
        }else{
           throw  new PasswordResetException(tokenVerificationResult);
        }
    }
   public SignoutResponse signout() {
       HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
       Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

       if (!principal.toString().equals("anonymousUser")) {
           Long userId = ((UserDetailsImpl) principal).getId();
           String refreshToken = jwtService.getJwtRefreshFromCookies(request);
           // Check if the token is valid before deleting
           if (refreshToken != null && refreshToken.length() > 0) {
               refreshTokenService.deleteByToken(refreshToken);
           }
       }
       ResponseCookie jwtCookie = jwtService.getCleanJwtCookie();
       ResponseCookie jwtRefreshCookie = jwtService.getCleanJwtRefreshCookies();

       return new SignoutResponse(
               jwtCookie.toString(),
               jwtRefreshCookie.toString(),
               new MessageResponse("You've been signed out!"));
   }

    public RefreshTokenResponse refreshToken(HttpServletRequest request) {
        String refreshToken = jwtService.getJwtRefreshFromCookies(request);
        //logger.info("Received refresh token: " + refreshToken);
        if((refreshToken == null) || refreshToken.isEmpty()){
            throw new RefreshTokenException("Refresh token is empty!");
        }else{
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtService.generateJwtCookie(user);
                        return new RefreshTokenResponse(jwtCookie.toString(),
                                new MessageResponse("Your access token is refreshed successfully!"));
                    }).orElseThrow(() -> new RefreshTokenException(refreshToken, "Refresh token is not in database"));
        }
       // return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty !"));
    }
}

