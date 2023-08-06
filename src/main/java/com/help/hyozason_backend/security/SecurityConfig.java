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
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()
                .cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeHttpRequests()
                .requestMatchers("/member/**").hasAnyRole("MEMBER", "MANAGER")
                .requestMatchers("/manager/**").hasAnyRole("MANAGER")
                .anyRequest().permitAll()
                .and()
                .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)
                .and()
                .addFilterBefore(new JwtExceptionFilter(),
                        JwtAuthenticationFilter.class)
        ;

        return http.build();
    }
}
