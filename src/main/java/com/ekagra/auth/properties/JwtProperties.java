package com.ekagra.auth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.jwt")
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtProperties {

    private String issuer;
    private long accessTokenExpirationMinutes;
    private long refreshTokenExpirationMinutes;
    private String publicKeyPath;
    private String privateKeyPath;

}

