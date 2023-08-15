package com.help.hyozason_backend.controller.jwt;

import com.help.hyozason_backend.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtController {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /*public String getUserEmail(HttpServletRequest request) throws Exception {
        try {
            return jwtTokenProvider.getMemberIdByToken(jwtTokenProvider.getAccessToken(request));
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // 예외를 다시 던져서 상위 메서드로 전달
        }
    }*/

    public String getUserEmail(HttpServletRequest request) {
        try {
            return jwtTokenProvider.getMemberIdByToken(jwtTokenProvider.getAccessToken(request));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve user email from JWT token.", e);
        }
    }

}

