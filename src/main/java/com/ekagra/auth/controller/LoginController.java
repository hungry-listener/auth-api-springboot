package com.ekagra.auth.controller;

import com.ekagra.auth.dtos.LoginRequest;
import com.ekagra.auth.service.LoginService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String showRegisterForm() {
        return "login"; // Page will use JS to submit form as JSON
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest httpRequest) {
        
        String token = loginService.authenticateAndGenerateToken(loginRequest, httpRequest);
        
        return ResponseEntity.ok().body(Map.of(
            "status", "success",
            "token", token
        ));
        
    }
}
