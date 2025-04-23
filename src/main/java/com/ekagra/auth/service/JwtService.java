package com.ekagra.auth.service;

import com.ekagra.auth.entity.User;
import com.ekagra.auth.properties.JwtProperties;
import com.ekagra.auth.utils.KeyUtils;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final KeyUtils keyUtils;
    private final JwtProperties jwtProperties;

    public JwtService(KeyUtils keyUtils, JwtProperties jwtProperties) {
        this.keyUtils = keyUtils;
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(User user) {
        try {
            PrivateKey privateKey = keyUtils.getPrivateKey();

            Instant now = Instant.now();
            Instant expiry = now.plusSeconds(jwtProperties.getAccessTokenExpirationMinutes()*60); 

            Map<String, Object> claims = new HashMap<>();
            claims.put("email", user.getEmail());
            claims.put("userName",user.getUsername());
            claims.put("userId", user.getId());
            claims.put("role", user.getRole().getName());
            claims.put("isApproved", user.getIsApproved());

            return Jwts
                    .builder()
                    .claims().empty().add(claims).and()
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(expiry))
                    .issuer(jwtProperties.getIssuer())
                    .signWith(privateKey,Jwts.SIG.RS256) 
                    .compact();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating token", e);
        }
    }
}

