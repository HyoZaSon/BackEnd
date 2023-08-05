package com.help.hyozason_backend.login.handler;

import com.help.hyozason_backend.jwt.service.JwtService;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
    private final JwtService jwtService;
    private final HelpUserRepository helpUserRepository;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        String email = extractUsername(authentication);
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(response,accessToken,refreshToken);

        helpUserRepository.findByEmail(email)
                .ifPresent(user->{
                    user.updateRefreshToken(refreshToken);
                    helpUserRepository.saveAndFlush(user);
                });
        log.info("로그인에 성공했습니다. 이메일 : {}",email);
        log.info("로그인에 성공했습니다. AccessToken: {}",accessToken);
        log.info("발급된 AccessToken 만료 기간 : {}",accessTokenExpiration);
    }

    private String extractUsername(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

}