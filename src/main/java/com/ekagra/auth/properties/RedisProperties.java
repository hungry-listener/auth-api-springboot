package com.ekagra.auth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ConfigurationProperties(prefix = "spring.data.redis")
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedisProperties {

    private String host;
    private int port;
    private String password;

}
