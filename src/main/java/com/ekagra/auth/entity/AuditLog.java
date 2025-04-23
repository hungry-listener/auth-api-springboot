package com.ekagra.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    private String userEmail;

    @Lob
    private String description;

    private LocalDateTime timestamp;

    private String ipAddress;

    @Lob
    private String userAgent;

    private String browser;

    private String operatingSystem;

    private String deviceType;

    private String country;

    private String region;

    private String city;

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
