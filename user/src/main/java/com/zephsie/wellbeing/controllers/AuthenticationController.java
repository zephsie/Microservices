package com.zephsie.wellbeing.controllers;

import com.zephsie.wellbeing.dtos.*;
import com.zephsie.wellbeing.events.OnRegistrationCompleteEvent;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.models.entity.VerificationToken;
import com.zephsie.wellbeing.security.UserDetailsImp;
import com.zephsie.wellbeing.security.jwt.JwtUtil;
import com.zephsie.wellbeing.services.api.IAuthenticationService;
import com.zephsie.wellbeing.services.entity.UserDetailsServiceImpl;
import com.zephsie.wellbeing.utils.converters.FieldErrorsToMapConverter;
import com.zephsie.wellbeing.utils.exceptions.BasicFieldValidationException;
import com.zephsie.wellbeing.utils.exceptions.InvalidCredentialException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    private final IAuthenticationService authenticationService;

    private final JwtUtil jwtUtil;

    private final ApplicationEventPublisher eventPublisher;

    private final FieldErrorsToMapConverter fieldErrorsToMapConverter;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    UserDetailsServiceImpl userDetailsService,
                                    IAuthenticationService authenticationService,
                                    JwtUtil jwtUtil,
                                    ApplicationEventPublisher eventPublisher,
                                    FieldErrorsToMapConverter fieldErrorsToMapConverter) {

        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
        this.eventPublisher = eventPublisher;
        this.fieldErrorsToMapConverter = fieldErrorsToMapConverter;
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json", name = "Login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO loginDTO,
                                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(fieldErrorsToMapConverter.map(bindingResult));
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        } catch (DisabledException e) {
            throw new AccessDeniedException("User is disabled");
        } catch (AuthenticationException e) {
            throw new InvalidCredentialException("Invalid credentials");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());

        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new TokenDTO(token));
    }

    @PostMapping(value = "/registration", consumes = "application/json", produces = "application/json", name = "Register")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid NewUserDTO personDTO,
                                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(fieldErrorsToMapConverter.map(bindingResult));
        }

        VerificationToken verificationToken = authenticationService.register(personDTO);

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(verificationToken));

        return ResponseEntity.ok(Map.of("message", "Registration successful. Please check your email for verification link."));
    }

    @PostMapping(value = "/verification", consumes = "application/json", produces = "application/json", name = "Verify")
    public ResponseEntity<TokenDTO> verify(@RequestBody @Valid VerificationDTO verificationDTO,
                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(fieldErrorsToMapConverter.map(bindingResult));
        }

        User user = authenticationService.verifyUser(verificationDTO);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new TokenDTO(token));
    }

    @PutMapping(value = "/access-token", consumes = "application/json", produces = "application/json", name = "Refresh access token")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody @Valid LoginDTO loginDTO,
                                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(fieldErrorsToMapConverter.map(bindingResult));
        }

        VerificationToken verificationToken = authenticationService.refreshVerificationToken(loginDTO);

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(verificationToken));

        return ResponseEntity.ok(Map.of("message", "Registration successful. Please check your email for verification link."));
    }

    @PostMapping(value = "/jwt-token", produces = "application/json", name = "Get JWT token")
    public ResponseEntity<UserIdDTO> verifyToken(@RequestBody @Valid TokenDTO tokenDTO,
                                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(fieldErrorsToMapConverter.map(bindingResult));
        }

        log.info("token: {}", tokenDTO.getToken());

        String email;

        try {
            email = jwtUtil.extractUsername(tokenDTO.getToken());
        } catch (Exception e) {
            throw new InvalidCredentialException("Invalid token");
        }

        if (email == null) {
            throw new InvalidCredentialException("Invalid token");
        }

        UserDetailsImp userDetails;

        try {
            userDetails = userDetailsService.loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            throw new InvalidCredentialException("User not found");
        }

        if (!jwtUtil.validateToken(tokenDTO.getToken(), userDetails)) {
            throw new InvalidCredentialException("Invalid token");
        }

        return ResponseEntity.ok(new UserIdDTO(userDetails.getUser().getId()));
    }
}