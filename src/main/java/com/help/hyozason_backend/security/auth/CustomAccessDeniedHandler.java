package com.help.hyozason_backend.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


    @Component
    public class CustomAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                           AccessDeniedException accessDeniedException) throws IOException, ServletException {
            if (accessDeniedException != null) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                long currentTimeMillis = System.currentTimeMillis();

                Map<String, Object> errorResponse = new LinkedHashMap<>();
                errorResponse.put("timestamp", currentTimeMillis);
                errorResponse.put("status", HttpStatus.FORBIDDEN.value());
                errorResponse.put("error", "Forbidden");
                errorResponse.put("message", "Access is denied");
                errorResponse.put("path", request.getRequestURI());

                ObjectMapper objectMapper = new ObjectMapper();
                String errorResponseJson = objectMapper.writeValueAsString(errorResponse);

                response.getWriter().write(errorResponseJson);
                response.getWriter().flush();
            }
        }
    }


