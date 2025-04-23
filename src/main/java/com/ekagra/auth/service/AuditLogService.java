package com.ekagra.auth.service;

import com.ekagra.auth.entity.AuditLog;
import com.ekagra.auth.repository.AuditLogRepository;
import com.ekagra.auth.utils.AuditLogUtils;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogUtils auditLogUtils;

    public AuditLogService(AuditLogRepository auditLogRepository, AuditLogUtils auditLogUtils){
        this.auditLogRepository = auditLogRepository;
        this.auditLogUtils = auditLogUtils;
    }

    public void logEvent(String eventType, String userEmail, String description, HttpServletRequest request) {
        auditLogUtils.initialize(request);
        AuditLog auditLog = AuditLog.builder()
                .eventType(eventType)
                .userEmail(userEmail)
                .description(description)
                .ipAddress(auditLogUtils.getIpAddress())
                .userAgent(auditLogUtils.getUserAgent())
                .browser(auditLogUtils.getBrowser())
                .operatingSystem(auditLogUtils.getOperatingSystem())
                .deviceType(auditLogUtils.getDeviceType())
                .country(auditLogUtils.getCountry())
                .region(auditLogUtils.getRegion())
                .city(auditLogUtils.getCity())
                .build();

        auditLogRepository.save(auditLog);
    }

    public String getClientIpAddress(HttpServletRequest request){
        return auditLogUtils.extractClientIpAddress(request);
    }
}
