package com.ekagra.auth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.email")
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailProperties {

    private String sender;
    private String activationEmailSubject;
    private String passwordResetEmailSubject;
    private String verificationTemplatePath;
    private String activationBaseUrl;
    private String passwordResetTemplatePath;
    private String passwordResetBaseUrl;
    
}
