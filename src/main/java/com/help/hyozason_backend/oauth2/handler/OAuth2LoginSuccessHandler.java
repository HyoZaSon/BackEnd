package com.help.hyozason_backend.oauth2.handler;


import com.help.hyozason_backend.etc.Role;
import com.help.hyozason_backend.jwt.service.JwtService;
import com.help.hyozason_backend.oauth2.CustomOAuth2User;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final HelpUserRepository helpUserRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공");
        try{
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            if(oAuth2User.getRole() == Role.Guest){
                String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
                response.addHeader(jwtService.getAccessHeader(),"Bearer"+accessToken);
                response.sendRedirect("oauth2/sign-up"); //추가 정보 입력 폼으로 리다이렉트
                jwtService.sendAccessAndRefreshToken(response,accessToken,null);
//                 HelpUserEntity = userRepository.findByEmail(oAuth2User.getEmail())
//                                .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));
//                findUser.authorizeUser();
            }else{
                loginSuccess(response,oAuth2User);
            }
        }catch (Exception e){
            throw e;
        }
    }

//     TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response,CustomOAuth2User oAuth2User) throws IOException{
        String accessToken=jwtService.createAccessToken(oAuth2User.getEmail());
        String  refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(),"Bearer"+accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer"+refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
    }
}
