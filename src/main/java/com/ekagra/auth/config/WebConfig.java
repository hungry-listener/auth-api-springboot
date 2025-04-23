package com.ekagra.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow all routes
                .allowedOrigins("http://localhost:3000/*") // Replace with your frontend origin
                .allowedMethods("GET", "POST")
                .allowedHeaders("*") // You can restrict this to only needed headers
                .allowCredentials(true)
                .maxAge(3600); // Cache CORS config for 1 hour
    }
}
