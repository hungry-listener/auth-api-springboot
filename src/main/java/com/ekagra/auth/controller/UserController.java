package com.ekagra.auth.controller;

import com.ekagra.auth.dtos.PasswordWrapper;
import com.ekagra.auth.dtos.ResetPasswordRequest;
import com.ekagra.auth.dtos.UserRegistrationRequest;
import com.ekagra.auth.dtos.UserUpdateRequest;
import com.ekagra.auth.entity.User;
import com.ekagra.auth.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Controller
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    // Registration page view (GET)
    @GetMapping("/registration")
    public String showRegisterForm() {
        return "register"; // Page will use JS to submit form as JSON
    }

    // Registration API (AJAX JSON submission)
    @PostMapping(value = "/register", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody UserRegistrationRequest request,
            BindingResult result
    ) {
        logger.info("Received Registration request for email: {}",request.getEmail());
        
        return userService.registerUserAndSendEmail(request,result);
        
    }

    // AJAX: Validate Email
    @GetMapping("/validate-email")
    @ResponseBody
    public ResponseEntity<?> validateEmail(@RequestParam String email) {
        logger.info("Received email validation request for: {}", email);
        Map<String, Object> response = new HashMap<>();

        userService.isEmailEmpty(email);

        userService.isDomainValid(email);

        userService.validateEmailAvailability(email);
        
        response.put("valid", true);
        response.put("message", "Email looks good!");
        return ResponseEntity.ok(response);
    }

    // AJAX: Validate Username
    @GetMapping("/validate-username")
    @ResponseBody
    public ResponseEntity<?> validateUsername(@RequestParam String username) {
        logger.info("Received username validation request for: {}", username);
        userService.isUserNameEmpty(username);
        userService.validateUsernameAvailability(username);
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("message", "Username is available!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate-password")
    @ResponseBody
    public ResponseEntity<?> validatePassword(@Validated PasswordWrapper passwordWrapper, BindingResult bindingResult) {
        logger.info("Received a password validation request");
        Map<String, Object> response = new HashMap<>();

        userService.validatePassword(bindingResult);

        response.put("valid", true);
        response.put("message", "Password meets all requirements.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/confirm-registration")
    @ResponseBody
    public ResponseEntity<?> confirmRegistration(@RequestParam String token) {
        Map<String, Object> response = new HashMap<>();
        userService.isRegistrationTokenValid(token);
        
        response.put("valid", true);
        response.put("message","Token is valid! User can login");
        userService.markTokenAsUsed(token);
        String email = userService.findEmailLinkedToActivationToken(token);
        userService.markUserAsActive(email);
        logger.info("Registration confirmation request processes for email: {}",email);
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/find-pending-users")
    @ResponseBody
    public ResponseEntity<?> findAllPendingApproval() {
        logger.info("Request to find pending user initiated.");

        Map<String, Object> response = new HashMap<>();

        List<User> userList = userService.findAllPendingApproval();

        List<Map<String, Object>> userDataList = new ArrayList<>();

        for (User user : userList) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", user.getEmail());
            userData.put("username", user.getUsername());
            userData.put("isActive", user.getIsActive());
            userData.put("isApproved", user.getIsApproved());
            userData.put("role", user.getRole().getName());
            userData.put("roleId", user.getRole().getId());
            userDataList.add(userData);
        }

        response.put("users", userDataList);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping(value = "/update-user", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<?> updateUserStatus(@RequestBody UserUpdateRequest request) {
        Map<String, Object> response = new HashMap<>();

        userService.updateUserStatus(request); 

        response.put("status", "success");
        response.put("message", "User status updated successfully");
        
        return ResponseEntity.ok(response);
    }

    
    @GetMapping(value = "/forgot-password")
    @ResponseBody
    public ResponseEntity<?> forgotPasswordRequest(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        userService.initiateForgotPasswordRequest(email); 

        response.put("status", "success");
        response.put("message", "Password reset email sent.");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/reset-password")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@RequestParam String resetToken) {
        Map<String, Object> response = new HashMap<>();
        userService.isResetPasswordTokenValid(resetToken);
        
        response.put("valid", true);
        response.put("message","Token is valid! User can reset their password.");
        
        logger.info("Password reset token is valid!");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/reset-password", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<?> submitNewPassword(@RequestBody ResetPasswordRequest request) {

        String resetToken = request.getResetToken();
        String newPassword =request.getNewPassword();

        userService.isResetPasswordTokenValid(resetToken);

        String email = userService.findEmailLinkedToPasswordResetToken(resetToken);

        userService.updatePassword(email, newPassword);
        userService.markPasswordResetTokenAsUsed(resetToken);

        return ResponseEntity.ok(Map.of("success", true, "message", "Password successfully updated"));
    }


}    
