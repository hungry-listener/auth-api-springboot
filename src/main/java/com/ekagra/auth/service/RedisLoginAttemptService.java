package com.ekagra.auth.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.ekagra.auth.properties.LoginSecurityProperties;

@Service
public class RedisLoginAttemptService {

    private final RedisTemplate<String, String> redisTemplate;
    private final LoginSecurityProperties loginSecurityProperties;

    public RedisLoginAttemptService(RedisTemplate<String, String> redisTemplate, LoginSecurityProperties loginSecurityProperties) {
        this.redisTemplate = redisTemplate;
        this.loginSecurityProperties = loginSecurityProperties;
    }

    private String getKey(String email, String ip) {
        return "failed_login:" + email.toLowerCase() + ":" + ip;
    }

    public void loginFailed(String email, String ip) {
        String key = getKey(email, ip);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        Long attempts = ops.increment(key); // increment count
        if (attempts == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(loginSecurityProperties.getLockDurationMinutes()));
        }
    }

    public boolean isLocked(String email, String ip) {
        String key = getKey(email, ip);
        String count = redisTemplate.opsForValue().get(key);
        return count != null && Integer.parseInt(count) >= loginSecurityProperties.getMaxFailedAttempts();
    }

    public void loginSucceeded(String email, String ip) {
        redisTemplate.delete(getKey(email, ip));
    }
}
