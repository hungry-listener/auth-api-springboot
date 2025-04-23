package com.ekagra.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ekagra.auth.filter.JwtAuthenticationFilter;
import com.ekagra.auth.utils.JwtUtils;
import com.ekagra.auth.utils.KeyUtils;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CustomAuthenticationEntryPoint entryPoint,
                                           CustomAccessDeniedHandler accessDeniedHandler,
                                           JwtUtils jwtUtils, KeyUtils keyUtils) throws Exception {
        return http
            .cors(cors -> {}) // enable CORS; actual config in WebConfig
            .csrf(csrf -> csrf.disable()) // disable CSRF for testing via Postman/curl
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/register",
                    "/api/auth/registration",
                    "/api/auth/login",
                    "/api/auth/validate-email",
                    "/api/auth/validate-username",
                    "/api/auth/validate-password",
                    "/api/auth/confirm-registration",
                    "/api/auth/confirmation-page.html",
                    "/api/auth/find-pending-users",
                    "/api/auth/forgot-password",
                    "/api/auth/reset-password",
                    "api/auth/update-user",
                    "/api/auth/.well-known/jwks.json",
                    "/css/style.css"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtils, keyUtils), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(entryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
