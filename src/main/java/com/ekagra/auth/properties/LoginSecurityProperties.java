package com.ekagra.auth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.security.login")
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginSecurityProperties {

    private int maxFailedAttempts;
    private long lockDurationMinutes;
    private boolean enableRedisLockout;
    private boolean accountApprovalEnabled;

}

