package com.help.hyozason_backend.jwt.filter;

import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.jwt.service.JwtService;

import com.help.hyozason_backend.jwt.util.PasswordUtil;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final String NO_CHECK_URL = "/login"; //login으로 들어오는 요청은 필터작동
    private final JwtService  jwtService;
    private final HelpUserRepository helpUserRepository;
    private GrantedAuthoritiesMapper authoritiesMapper= new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws
            ServletException, IOException{
        if(request.getRequestURI().equals(NO_CHECK_URL)){
            filterChain.doFilter(request,response); //"login"요청이 들어오면 다음필터 호출
            return;  //현재 필터 진행 막기
        }
        //refreshToken 추출
        //유효하지 않거나 없다면 null 반환
        //accessToken 만료되었을 때만 refreshToken으로 요청하지 때문에 추출한 refreshTokendms null
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid) //유효한 refreshToken qkghks
                .orElse(null);

     //리프레시 토큰이 헤더에 존재한다면, 사용자가 accessToken만료되어서 보낸것.
        //리프레시 토큰이 DB의 리프레시 토큰과 일치하는지 판단 후 일치하면 accessToken 재발급
        if(refreshToken!= null){
            checkRefreshTokenAndReIssueAccessToken(response,refreshToken);
            return;
        }
        //refreshToken이 없거나 유효하지 않다면, accessToken 으로 검사하고 인증 처리하는 로직 수행
        //accessToken 유효하지 않으면 , 403에러 발생
        //유효하다면 인증객체 담긴 상태로 다음 필터로 넘어가서 인증성공
        if(refreshToken==null){
            checkAccessTokenAndAuthentication(request,response,filterChain);
        }
    }
    //리프레시 토큰으로 유저정보 찾기, 엑세토큰/리프레시 토큰 재발 메소드
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response,String refreshToken){
        helpUserRepository.findByRefreshToken(refreshToken)
                .ifPresent(user-> {
                    String reIssuedRefreshToken = reIssueRefreshToken(user);
                    jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getEmail()),reIssuedRefreshToken );
                });
    }

    private String reIssueRefreshToken(HelpUserEntity user){
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        helpUserRepository.saveAndFlush(user);
        return reIssuedRefreshToken;
    }


    //액세스 토큰 체크, 인증처리 메서드
    public void checkAccessTokenAndAuthentication(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) throws ServletException,IOException{
        log.info("checkAccessTokenAndAuthentication () 호출");
        jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .ifPresent(accessToken -> jwtService.extractEmail(accessToken)
                        .ifPresent(email -> helpUserRepository.findByEmail(email)
                                .ifPresent(this::saveAuthentication)));

        filterChain.doFilter(request,response);
    }

    //UserDetailUser를 Builder 로 생성후 인증처리해서
    //SecurityContextHolder 에 담아서 인증처리
    //소셜로그인은 인층 처리시 null 이면 안되므로 임의로 부여
    public void saveAuthentication(HelpUserEntity user){
        String password = user.getPassword();
        if(password == null){
            password = PasswordUtil.generateRandomPassword();
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getNickName())
                .password(password)
                .roles(user.getRole().name())
                .build();

        Authentication authentication=
                new UsernamePasswordAuthenticationToken(userDetails,null,authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }



}
