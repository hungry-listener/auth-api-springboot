package com.ekagra.auth.controller;

import com.ekagra.auth.utils.KeyUtils;
import com.ekagra.auth.utils.JwkUtils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
@RequestMapping("api/auth/.well-known") // Convention for JWKS
public class JwksController {

    private final KeyUtils keyUtils;

    public JwksController(KeyUtils keyUtils) {
        this.keyUtils = keyUtils;
    }

    @GetMapping("/jwks.json")
    public Map<String, Object> getJwks() {
        try {
            RSAPublicKey publicKey = (RSAPublicKey) keyUtils.getPublicKey();
            Map<String, Object> jwk = JwkUtils.generateJwk(publicKey);

            return Map.of("keys", new Map[]{jwk});
        } catch (Exception e) {
            
            // Throw a proper REST exception
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to retrieve JWKS",
                e
            );
        }
    }

}

