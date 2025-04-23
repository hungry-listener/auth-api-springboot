package com.ekagra.auth.utils;


import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;

public class JwkUtils {

    public static Map<String, Object> generateJwk(RSAPublicKey publicKey) {
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

        String modulus = encoder.encodeToString(publicKey.getModulus().toByteArray());
        String exponent = encoder.encodeToString(publicKey.getPublicExponent().toByteArray());

        return Map.of(
            "kty", "RSA",
            "alg", "RS256",
            "use", "sig",
            "kid", "auth-service-key",  // Optional: used for key rotation
            "n", modulus,
            "e", exponent
        );
    }
}

