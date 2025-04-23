package com.ekagra.auth.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.*;
import java.util.Base64;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.ekagra.auth.properties.JwtProperties;

@Component
public class KeyUtils {

    private final JwtProperties jwtProperties;
    private final ResourceLoader resourceLoader;

    public KeyUtils(JwtProperties jwtProperties, ResourceLoader resourceLoader){
        this.jwtProperties = jwtProperties;
        this.resourceLoader = resourceLoader;
    }

    public PrivateKey getPrivateKey() throws Exception{
        
            Resource resource = resourceLoader.getResource(jwtProperties.getPrivateKeyPath());
            InputStream is = resource.getInputStream();
            String key = new String(is.readAllBytes())
                    .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        
    }

    public PublicKey getPublicKey() throws Exception{
        
            Resource resource = resourceLoader.getResource(jwtProperties.getPublicKeyPath());
            InputStream is = resource.getInputStream();
            String key = new String(is.readAllBytes())
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        
    }
}

