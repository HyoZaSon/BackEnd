package com.help.hyozason_backend.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Optional;


@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;


    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    private final HelpUserRepository userRepository;

    public String createAccessToken(String email){
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime()+accessTokenExpirationPeriod))
                .withClaim(EMAIL_CLAIM,email)
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String createRefreshToken(){
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime()+refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    //accessToken 재발급시 헤더에 넣어서 보내는 메소드
    public void sendAcessToken(HttpServletResponse response, String accessToken)
    {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(accessHeader,accessToken);
        log.info("재발급된 AccessToken:{}",accessToken);
    }

    //로그인시 accessToken, refreshToken 보내는 메소드

    public void sendAccessAndRefreshToken(HttpServletResponse response,String accessHeader,String refreshHeader){
        response.setStatus(HttpServletResponse.SC_OK);

        response.setHeader(accessHeader,accessHeader);
        response.setHeader(refreshHeader,refreshHeader);
        log.info("AccessToken, RefreshToken 헤더 설정 완료");
    }
    //헤더에서 refreshToken 추출
    public Optional<String> extractRefreshToken(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken->refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER,""));
    }
    //헤어에서 accessToken 추출
    public Optional<String> extractAccessToken(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(refreshToken->refreshToken.startsWith(BEARER))
                .map(refreshToken->refreshToken.replace(BEARER,""));
    }

    public Optional<String> extractEmail(String accessToken){
        try{
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()//반환된 빌더로 JWT verifier생성
                    .verify(accessToken)//accessToken 검증하고 유효하지 않으면 예외 발생
                    .getClaim(EMAIL_CLAIM) //Claim 가져오기
                    .asString()); //String 변환후 유저 Email반환
        }catch (Exception e){
            log.error("엑세스 토큰이 유효하지 않습니다");
            return Optional.empty();
        }

    }
//DB에 refreshToken 업데이트 하는 메소드
    public void updateRefreshToken(String email,String refreshToken){
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user->user.updateRefreshToken(refreshToken),
                        () -> new Exception("일치하는 회원이 없습니다")
                );
    }
    //토큰의 유효성 검사하는 메소드
    public boolean isTokenValid(String token){
        try{
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        }catch (Exception e){
            log.error("유효하지 않은 토큰입니다.{}",e.getMessage());
            return false;
        }
    }

}
