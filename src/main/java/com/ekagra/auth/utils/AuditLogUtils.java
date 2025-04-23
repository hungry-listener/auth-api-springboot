package com.ekagra.auth.utils;

import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AuditLogUtils {

    private String ipAddress;
    private String userAgentString;
    private UserAgent userAgent;
    private final GeoLocationUtil geoLocationUtil;
    private GeoLocationUtil.GeoInfo geoInfo;

    public AuditLogUtils(GeoLocationUtil geoLocationUtil) {
        this.geoLocationUtil = geoLocationUtil;
    }

    public void initialize(HttpServletRequest request) {
        this.userAgentString = request.getHeader("User-Agent");
        this.userAgent = UserAgent.parseUserAgentString(userAgentString);
        this.ipAddress = extractClientIpAddress(request);
        this.geoInfo = geoLocationUtil.getGeoInfo(this.ipAddress);
    }

    public String extractClientIpAddress(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim(); // First IP in list
        }
        return request.getRemoteAddr();
    }

    public String getUserAgent() {
        return userAgentString;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getBrowser() {
        return userAgent.getBrowser() != null ? userAgent.getBrowser().getName() : "Unknown";
    }

    public String getOperatingSystem() {
        return userAgent.getOperatingSystem() != null ? userAgent.getOperatingSystem().getName() : "Unknown";
    }

    public String getDeviceType() {
        return userAgent.getOperatingSystem() != null ?
                userAgent.getOperatingSystem().getDeviceType().getName() : "Unknown";
    }

    public String getCountry() {
        return geoInfo != null ? geoInfo.country() : "Unknown";
    }

    public String getRegion() {
        return geoInfo != null ? geoInfo.region() : "Unknown";
    }

    public String getCity() {
        return geoInfo != null ? geoInfo.city() : "Unknown";
    }
}
