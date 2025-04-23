package com.ekagra.auth.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;


public class HandleJwtFilterExceptions {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void handleKeyLoadingException(HttpServletResponse response, KeyLoadingException ex) throws IOException {
        writeErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    public void handleTokenExpired(HttpServletResponse response, TokenExpiredException ex) throws IOException {
        writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
    }

    public void handleInvalidToken(HttpServletResponse response, RuntimeException ex) throws IOException {
        writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
    }

    public void handleAllOtherExceptions(HttpServletResponse response, Exception ex) throws IOException{
        writeErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, Object> body = Map.of(
                "status", status,
                "error", message
        );

        objectMapper.writeValue(response.getWriter(), body);
    }
}
