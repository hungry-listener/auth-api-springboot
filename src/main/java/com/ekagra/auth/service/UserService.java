package com.ekagra.auth.service;

import com.ekagra.auth.entity.EmailConfirmationToken;
import com.ekagra.auth.entity.PasswordReset;
import com.ekagra.auth.entity.Role;
import com.ekagra.auth.entity.User;
import com.ekagra.auth.exceptions.EmailAlreadyExistsException;
import com.ekagra.auth.exceptions.EmailNotFoundException;
import com.ekagra.auth.exceptions.InvalidEmailException;
import com.ekagra.auth.exceptions.InvalidTokenException;
import com.ekagra.auth.exceptions.PasswordException;
import com.ekagra.auth.exceptions.RoleNotFoundException;
import com.ekagra.auth.exceptions.UserAlreadyExistsException;
import com.ekagra.auth.exceptions.UserNotFoundException;
import com.ekagra.auth.exceptions.UsernameEmptyException;
import com.ekagra.auth.repository.EmailConfirmationTokenRepository;
import com.ekagra.auth.repository.PasswordResetRepository;
import com.ekagra.auth.repository.RoleRepository;
import com.ekagra.auth.dtos.UserRegistrationRequest;
import com.ekagra.auth.dtos.UserUpdateRequest;
import com.ekagra.auth.repository.UserRepository;
import com.ekagra.auth.utils.EmailUtils;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


@Service
public class UserService {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final EmailUtils emailUtils;

    private final EmailService emailService;

    private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;

    private final PasswordResetRepository passwordResetRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Value("${app.email.activation-base-url}")
    private String activationBaseUrl;


    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, 
                        RoleRepository roleRepository, EmailUtils emailUtils,
                        EmailConfirmationTokenRepository emailConfirmationTokenRepository,
                        EmailService emailService, PasswordResetRepository passwordResetRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailUtils = emailUtils;
        this.emailConfirmationTokenRepository = emailConfirmationTokenRepository;
        this.emailService = emailService;
        this.passwordResetRepository = passwordResetRepository;
    }

    @Transactional
    public ResponseEntity<?> registerUserAndSendEmail(UserRegistrationRequest request, BindingResult result) {
        String email = request.getEmail();
        String username = request.getUsername();
        String password = request.getPassword();
        validateRegistrationRequest(request, result);

        if (result.hasErrors()) {
                
            Map<String, List<Map<String, String>>> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.computeIfAbsent(error.getField(), k -> new ArrayList<>())
                      .add(Map.of(
                          "code", error.getCode(),
                          "message", error.getDefaultMessage()
                      ));
            }

            return ResponseEntity.badRequest().body(errors);
        }

        Role defaultRole = roleRepository.findByName("viewer")
            .orElseThrow(() -> new RoleNotFoundException("Default role not found"));


        
        String hashedPassword = passwordEncoder.encode(password);

        User newUser = User.builder()
                .email(email)
                .username(username)
                .password(hashedPassword)
                .role(defaultRole)
                .build();

        userRepository.save(newUser);
        generateConfirmationTokenAndSendEmail(request);
        logger.info("New user with email: {} registered.",email);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "User registered successfully!");
        return ResponseEntity.ok(response);
    }

    @Transactional
    public EmailConfirmationToken generateConfirmationTokenAndSendEmail(UserRegistrationRequest request) {
        String email = request.getEmail();
        
        
        String token = UUID.randomUUID().toString();


        EmailConfirmationToken confirmationToken = EmailConfirmationToken.builder()
                                                    .email(email)
                                                    .token(token)
                                                    .expiryDate(LocalDateTime.now().plusHours(24))
                                                    .build();

        
        emailService.sendActivationEmail(email, token); 
        logger.info("Confirmation toke sent to email: {}",email);                                                        

        return emailConfirmationTokenRepository.save(confirmationToken);
    }

    public void validateRegistrationRequest(UserRegistrationRequest request, BindingResult result) {
        //Some of the validations are done in the UserRegistrationRequest DAO 
        isDomainValid(request.getEmail(), result);
        validateEmailAvailability(request.getEmail(), result);
        validateUsernameAvailability(request.getUsername(), result);
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "NoMatch", "Passwords do not match");
        }
    }


    public void validateEmailAvailability(String email) {
        boolean exists = userRepository.findByEmail(email).isPresent();
        if(exists){
            throw new EmailAlreadyExistsException(email+" Id already exists.");
        }
    }

    public void validateEmailAvailability(String email, BindingResult result) {
        boolean exists = userRepository.findByEmail(email).isPresent();
        if (exists) {
            result.rejectValue(
                "email",
                "AlreadyRegistered",
                "This email address is already registered. Please use a different email or log in."
            );
        }
    }
    
    public void validateUsernameAvailability(String username) {
        boolean exists = userRepository.findByUsername(username).isPresent();
        if(exists){
            throw new UserAlreadyExistsException("Username "+username+ " is already taken.") ;
        }  
    }

    public void validateUsernameAvailability(String username, BindingResult result) {
        boolean exists = userRepository.findByUsername(username).isPresent();
        if(exists){
            result.rejectValue("username", "AlreadyTaken", "Username is already taken.");
        }  
    }
    
    public void isDomainValid(String email) {
        boolean valid = emailUtils.isDomainValid(email);
        if(!valid){
            throw new InvalidEmailException("Invalid email Id");
        }
    }

    public void isDomainValid(String email, BindingResult result) {
        boolean valid = emailUtils.isDomainValid(email);
        if(!valid){
            result.rejectValue("email", "InvalidDomain", "Email domain is invalid.");
        }
    }

    public void isEmailEmpty(String email) {
        if(email == null || email.trim().isEmpty()){
            throw new InvalidEmailException("Email cannot be empty.");
        }
    }
    

    public void isUserNameEmpty(String username) {
        boolean isEmpty = username.trim().isEmpty();
        if(isEmpty){
            throw new UsernameEmptyException("Username cannot be empty.");
        }
    }


    public void validatePassword(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldError("password");
            String message = (error != null) ? error.getDefaultMessage() : "Invalid password.";
            throw new PasswordException(message);
        }
    }


    public void isRegistrationTokenValid(String token) {
        LocalDateTime now =LocalDateTime.now();
        boolean isValid = emailConfirmationTokenRepository.existsByTokenAndUsedFalseAndExpiryDateAfter(token, now);
        if(!isValid){
            throw new InvalidTokenException("Invalid, used or expired token. Please request a new activation link.");
        }
    }

    public boolean isResetPasswordTokenValid(String token) {
        LocalDateTime now =LocalDateTime.now();
        boolean isValid = passwordResetRepository.tokenIsValid(token, now);
        
        if(!isValid){
            throw new InvalidTokenException("Invalid or expired token. Please request a new Reset Password link.");
        }
        return isValid;
    }

    public void markTokenAsUsed(String token){
        emailConfirmationTokenRepository.markTokenAsUsed(token);
    }

    public void markPasswordResetTokenAsUsed(String resetToken){
        passwordResetRepository.markTokenAsUsed(resetToken);
    }

    public void markUserAsActive(String email){
        userRepository.markUserAsActive(email);
    }

    public String findEmailLinkedToActivationToken(String token){
        String email = emailConfirmationTokenRepository.findEmailByToken(token);
        return email;
    }

    public String findEmailLinkedToPasswordResetToken(String resetToken){
        String email = passwordResetRepository.findEmailByPasswordResetToken(resetToken);
        return email;
    }

    public List<User> findAllPendingApproval(){
        List<User> userList = userRepository.findAllPendingApproval();
        return userList;
    }

    public void updateUserStatus(UserUpdateRequest request) {
        int updatedRows = userRepository.updateUserStatus(
            request.getRoleId(),
            request.getIsActive(),
            request.getIsApproved(),
            request.getEmail()
        );
        if(!(updatedRows > 0)){
            throw new UserNotFoundException("User not found or could not be updated");
        }
        logger.info("User statuse for email: {} updated successfully.", request.getEmail());
    }

    public void initiateForgotPasswordRequest(String email){
        isDomainValid(email);
        Optional<User> user = userRepository.findByEmail(email);

        if(!user.isPresent()){
            throw new EmailNotFoundException("Email id not registered.");
        }
             
        String token = UUID.randomUUID().toString();
        PasswordReset passwordResetToken = PasswordReset.builder()
                                                    .user(user.get())
                                                    .resetToken(token)
                                                    .createdAt(LocalDateTime.now())
                                                    .expiresAt(LocalDateTime.now().plusMinutes(15))
                                                    .build();

        
        emailService.sendPasswordResetEmail(email, user.get().getUsername(), token); 
        logger.info("Password reset token sent to email: {}",email);                                                        

        passwordResetRepository.save(passwordResetToken);
    }

    public void updatePassword(String email, String newPassword){
        String hashedPassword = passwordEncoder.encode(newPassword);
        userRepository.updatePassword(email, hashedPassword);
    }
}
