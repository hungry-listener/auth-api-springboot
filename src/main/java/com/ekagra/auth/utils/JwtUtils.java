package com.ekagra.auth.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;


import java.security.PublicKey;

import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    public Claims parseToken(String token, PublicKey publicKey) throws Exception{
        
            return Jwts
                .parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
    }
}
