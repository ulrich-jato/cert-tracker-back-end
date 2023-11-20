package com.devops.certtracker.controller;

import com.devops.certtracker.dto.request.SigninRequest;
import com.devops.certtracker.dto.request.SignupRequest;
import com.devops.certtracker.dto.response.MessageResponse;
import com.devops.certtracker.dto.response.SigninResponse;
import com.devops.certtracker.dto.response.UserInfoResponse;
import com.devops.certtracker.exception.EmailAlreadyInUseException;
import com.devops.certtracker.service.AuthenticationService;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister() throws Exception{
        SignupRequest signupRequest = createSignupRequest();
        MessageResponse messageResponse = new MessageResponse(
                "User registered successfully! Check your email to complete your registration");
        when(authenticationService.register(any(SignupRequest.class), any(HttpServletRequest.class)))
                .thenReturn(messageResponse);
        String requestJson = objectMapper.writeValueAsString(signupRequest);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(messageResponse.getMessage()));
    }
    @Test
    public void testRegisterEmailAlreadyExists() throws Exception {
        // Given
        SignupRequest signupRequest = createSignupRequest();
        when(authenticationService.register(any(SignupRequest.class), any(HttpServletRequest.class)))
                .thenThrow(new EmailAlreadyInUseException("Email is already in use!"));

        // When
        String requestJson = objectMapper.writeValueAsString(signupRequest);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Email is already in use!"));
    }

    @Test
    public void testAuthenticate() throws Exception{
        SigninRequest signinRequest = createSigninRequest();
        SigninResponse signinResponse = createSigninResponse();
        when(authenticationService.authenticate(any(SigninRequest.class))).thenReturn(signinResponse);
        String requestJson = objectMapper.writeValueAsString(signinRequest);
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(signinResponse.getUserInfoResponse().getId()))
                .andExpect(jsonPath("$.firstname").value(signinResponse.getUserInfoResponse().getFirstname()))
                .andExpect(jsonPath("$.lastname").value(signinResponse.getUserInfoResponse().getLastname()))
                .andExpect(jsonPath("$.email").value(signinResponse.getUserInfoResponse().getEmail()))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0]").value(signinResponse.getUserInfoResponse().getRoles().get(0)))
                .andExpect(cookie().value("cert-tracker-jwt","abc-access-token" ))
                .andExpect(cookie().value("cert-tracker-jwt-refresh","abc-refresh-token" ))
                .andExpect(cookie().path("cert-tracker-jwt","/api"))
                .andExpect(cookie().path("cert-tracker-jwt-refresh","/api/auth"));
                //.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testAuthenticateWithBadCredentials() throws Exception {
        // Create a SigninRequest
        SigninRequest signinRequest = createSigninRequest();
        SigninResponse signinResponse = createSigninResponse();

        when(authenticationService.authenticate(any(SigninRequest.class)))
                .thenThrow(new BadCredentialsException("Bad Credentials"));
        // Convert SigninRequest to JSON
        String requestJson = objectMapper.writeValueAsString(signinRequest);

        // Perform the authentication request
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized()) // Expecting unauthorized status
                .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
                .andExpect(jsonPath("$.error").value("Unauthorized access"))
                .andExpect(jsonPath("$.message").value("Bad Credentials"));
    }

    @Test
    public void testAuthenticateWithAccountNotEnabled() throws Exception {
        // Create a SigninRequest
        SigninRequest signinRequest = createSigninRequest();
        SigninResponse signinResponse = createSigninResponse();

        when(authenticationService.authenticate(any(SigninRequest.class)))
                .thenThrow(new DisabledException("User account is disabled"));
        // Convert SigninRequest to JSON
        String requestJson = objectMapper.writeValueAsString(signinRequest);

        // Perform the authentication request
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden()) // Expecting unauthorized status
                .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_FORBIDDEN))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("User account is disabled"));
    }


    private SigninResponse createSigninResponse() {
        ResponseCookie jwtCookie = generateCookie("cert-tracker-jwt", "abc-access-token","/api");
        ResponseCookie jwtRefreshCookie = generateCookie(
                "cert-tracker-jwt-refresh", "abc-refresh-token","/api/auth");
        SigninResponse signinResponse = new SigninResponse();
        UserInfoResponse userInfoResponse =  createUserInfoResponse();
        signinResponse.setJwtCookie(jwtCookie.toString());
        signinResponse.setJwtRefreshCookie(jwtRefreshCookie.toString());
        signinResponse.setUserInfoResponse(userInfoResponse);
        return signinResponse;
    }

    private ResponseCookie generateCookie(String name, String value, String path){
        return ResponseCookie.from(name, value).path(path).maxAge(24 * 60 * 60).httpOnly(true).build();
    }

    private UserInfoResponse createUserInfoResponse() {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setId(123L);
        userInfoResponse.setFirstname("John");
        userInfoResponse.setLastname("Doe");
        userInfoResponse.setEmail("john.doe@example.com");
        userInfoResponse.setRoles(List.of("ROLE_USER" ));
        return  userInfoResponse;
    }

    private SigninRequest createSigninRequest() {
        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setEmail("test@gmail.com");
        signinRequest.setPassword("password123");
        return signinRequest;
    }

    private SignupRequest createSignupRequest() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFirstname("John");
        signupRequest.setLastname("Doe");
        signupRequest.setEmail("test@gmail.com");
        signupRequest.setPassword("password123");
        return signupRequest;
    }
}
