package com.ekagra.auth;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ekagra.auth.properties.EmailProperties;
import com.ekagra.auth.properties.JwtProperties;
import com.ekagra.auth.properties.LoginSecurityProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    EmailProperties.class,
    JwtProperties.class,
    LoginSecurityProperties.class
})
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}
