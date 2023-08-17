package com.help.hyozason_backend.security;


import com.help.hyozason_backend.security.auth.CustomAccessDeniedHandler;
import com.help.hyozason_backend.security.jwt.JwtAuthenticationFilter;
import com.help.hyozason_backend.security.jwt.JwtExceptionFilter;
import com.help.hyozason_backend.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //JwtAuthenticationFilter 등록(JWT 토큰의 존재 여부와 유효성을 검증)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .csrf().disable() //CSRF 비활성화
                .cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션 유지 X
                .and()
                .formLogin().disable() //폼 로그인 비활성화
                .httpBasic().disable() //HTTP기본 인증 비활성화
                .authorizeHttpRequests() //각 엔드포인트에 대한 접근 권한을 설정
//                .requestMatchers("/help/**").hasAnyRole("HELPER","HELP", "MANAGER") // 특정 URL 패턴에 대한 접근 권한을 부여, 역할 지정
                .requestMatchers("/help/**").permitAll()
                .requestMatchers("/helper/**").hasAnyRole("HELP","HELPER","MANAGER")
                .requestMatchers("/user-info").permitAll()
                .requestMatchers("/user").hasAnyRole("HELP", "HELPER","MANAGER")
                .anyRequest().permitAll() //다른 모든 요청 모든 사용자에게 허용
                .and()
                .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)
                .and()
                .addFilterBefore(new JwtExceptionFilter(),
                        JwtAuthenticationFilter.class)
        ;

        return http.build();
    }
}
