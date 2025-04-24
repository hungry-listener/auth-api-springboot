package com.ekagra.auth.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

@Component
public class GeoLocationUtil {

    private DatabaseReader dbReader;
    private final ResourceLoader resourceLoader;

    @Value("${app.geolite.path}")
    private String geoLitePath;

    public GeoLocationUtil(ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() throws IOException {
        // Load from classpath instead of file system
        
        Resource resource = resourceLoader.getResource(geoLitePath);

        try (InputStream dbStream = resource.getInputStream()) {
            dbReader = new DatabaseReader.Builder(dbStream).build();
        }
    }

    public GeoInfo getGeoInfo(String ipAddress) {
        try {
            InetAddress ip = InetAddress.getByName(ipAddress);
            CityResponse response = dbReader.city(ip);

            String country = response.getCountry().getName();
            String region = response.getMostSpecificSubdivision().getName();
            String city = response.getCity().getName();

            return new GeoInfo(country, region, city);

        } catch (IOException | GeoIp2Exception e) {
            return new GeoInfo("Unknown", "Unknown", "Unknown");
        }
    }

    public record GeoInfo(String country, String region, String city) {}
}
