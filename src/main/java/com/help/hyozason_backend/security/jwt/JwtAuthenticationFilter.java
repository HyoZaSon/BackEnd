package com.help.hyozason_backend.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;


    //들어오는 모든 요청마다 JWT 토큰의 존재 여부와 유효성을 확인하는 역할
    @RequiredArgsConstructor
    public class JwtAuthenticationFilter extends GenericFilterBean {

        private final JwtTokenProvider jwtTokenProvider;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            //refreshToken 헤더에서 추출해 반환
            String refreshToken = jwtTokenProvider.resolveRefreshToken((HttpServletRequest) request);
            // //AUTHORIZATION_HEADER 추출해 반환
            String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

            if (refreshToken != null && ((HttpServletRequest) request).getRequestURI()
                    .equals("/user/refresh") && jwtTokenProvider.validateRefreshToken(refreshToken)) {
                System.out.println("refreshToken 정보 확인 +유효성 검사 통과");
                Authentication authentication = jwtTokenProvider.getRefreshAuthentication(refreshToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (token != null && jwtTokenProvider.validateToken(token)) {
                System.out.println("accessToken 정보 확인 +유효성 검사 통과");
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                if (authentication != null && authentication.isAuthenticated()) {
                    // 사용자가 인증되어 있는 경우
                    System.out.println("authentication~~~~~~~~``" +authentication);
                }else{
                    System.out.println("authentication error!!!!!!");
                }

            }

            chain.doFilter(request, response);
        }
}
