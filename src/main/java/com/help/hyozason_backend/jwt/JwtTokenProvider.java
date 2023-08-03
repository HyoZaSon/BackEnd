package com.help.hyozason_backend.jwt;

import io.jsonwebtoken.Jwts;

public class JwtTokenProvider {
    private String accessSecretKey = "Haesong";
    public void validateToken(String authorization) {
    }
    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).getBody().getSubject();
    }

}
