package com.ekagra.auth.service;

import com.ekagra.auth.dtos.LoginRequest;
import com.ekagra.auth.entity.User;
import com.ekagra.auth.exceptions.PasswordException;
import com.ekagra.auth.exceptions.UserApprovalException;
import com.ekagra.auth.exceptions.UserNotFoundException;
import com.ekagra.auth.exceptions.AccountLockedException;
import com.ekagra.auth.properties.LoginSecurityProperties;
import com.ekagra.auth.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class LoginService {

    private final LoginSecurityProperties loginSecurityProperties;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;
    private final RedisLoginAttemptService redisLoginAttemptService;

    public LoginService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, 
                        JwtService jwtService, AuditLogService auditLogService, RedisLoginAttemptService redisLoginAttemptService, LoginSecurityProperties loginSecurityProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; 
        this.jwtService = jwtService;
        this.auditLogService = auditLogService;
        this.redisLoginAttemptService = redisLoginAttemptService;
        this.loginSecurityProperties = loginSecurityProperties;
    }

    public String authenticateAndGenerateToken(LoginRequest request, HttpServletRequest httpRequest) {
        String email = request.getEmail();
        String enteredPassword = request.getPassword();
        String clientIP = auditLogService.getClientIpAddress(httpRequest);

        Optional<User> userOptional = userRepository.findByEmail(email);

        if(redisLoginAttemptService.isLocked(email, clientIP)){
            updateAuditLogs("LOGIN_FAILED", email, "Account is locked due to too many failed attempts.", httpRequest);
            throw new AccountLockedException("You account is locked due to too many failed attempts.");
        }
        
        if (userOptional.isEmpty()) {
            updateAuditLogs("LOGIN_FAILED",email,"No user registered with this email.",httpRequest);
            redisLoginAttemptService.loginFailed(email, clientIP);
            throw new UserNotFoundException("No user registered with this email.");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(enteredPassword, user.getPassword())) {
            updateAuditLogs("LOGIN_FAILED",email,"Invalid credentials.",httpRequest);
            redisLoginAttemptService.loginFailed(email, clientIP);
            throw new PasswordException("Invalid credentials.");
        }

        // Add check if user is active
        if (!user.getIsActive()) {
            updateAuditLogs("LOGIN_FAILED",email,"Your account is not yet active.",httpRequest);
            redisLoginAttemptService.loginFailed(email, clientIP);
            throw new UserApprovalException("Your account is not yet active. Click the activation link in your email.");
        }

        // Add check if user is approved
        if(loginSecurityProperties.isAccountApprovalEnabled()){
            if (!user.getIsApproved()) {
                updateAuditLogs("LOGIN_FAILED",email,"Your account is not yet approved.",httpRequest);
                redisLoginAttemptService.loginFailed(email, clientIP);
                throw new UserApprovalException("Your account is not yet approved.");
            }
        }

        updateAuditLogs("LOGIN_SUCCESS",email,"User logged in successfully",httpRequest);
        redisLoginAttemptService.loginSucceeded(email, clientIP);
        return jwtService.generateToken(user);
    }

    public void updateAuditLogs(String eventType, String email, String description, HttpServletRequest httpRequest){
        auditLogService.logEvent( eventType, email, description, httpRequest);
    }
}
