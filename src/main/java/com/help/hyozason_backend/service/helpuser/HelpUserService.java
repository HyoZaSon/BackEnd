package com.help.hyozason_backend.service.helpuser;


import com.help.hyozason_backend.dto.helpuser.MemberRequestDto;
import com.help.hyozason_backend.dto.helpuser.MemberResponseDto;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;

import com.help.hyozason_backend.exception.BaseException;
import com.help.hyozason_backend.exception.MemberErrorCode;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;
import com.help.hyozason_backend.security.jwt.JwtTokenProvider;
import com.help.hyozason_backend.service.helpoauth.HelpOauthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.*;




@Service
@Slf4j
@RequiredArgsConstructor
public class HelpUserService  {

    private final JwtTokenProvider jwtTokenProvider;

    private final HelpOauthService helpOauthService;
    private final HelpUserRepository helpUserRepository;

    public void save(HelpUserEntity member) {
        helpUserRepository.save(member);
    }

    public HelpUserEntity register(MemberRequestDto.RegisterMember registerMember) {
        HelpUserEntity member = new HelpUserEntity(
                registerMember.getEmail());
        save(member);
        return member;
    }

    public MemberResponseDto.TokenInfo registerMember(MemberRequestDto.RegisterMember registerMember) {
        checkRegister(registerMember);
        HelpUserEntity member = register(registerMember);
        MemberResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getUserEmail());
        member.changeRefreshToken(tokenInfo.getRefreshToken());
        return tokenInfo;
    }


    public MemberResponseDto.TokenInfo socialLogin(
                                                   MemberRequestDto.SocialLoginToken socialLoginToken) throws BaseException, IOException {
        String idToken = socialLoginToken.getIdToken();
        String email = helpOauthService.getKaKaoEmail(helpOauthService.getKaKaoAccessToken(idToken));

        HelpUserEntity member = helpUserRepository.findByUserEmail(email);
        if (member != null) {
            MemberResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getUserEmail());
            member.changeRefreshToken(tokenInfo.getRefreshToken());
            return tokenInfo;
        } else {
            BaseException exception = new BaseException(MemberErrorCode.UN_REGISTERED_MEMBER);
            exception.setEmailMessage(email);
            throw exception;
        }
    }

    public void checkRegister(MemberRequestDto.RegisterMember registerMember) {
        Boolean flag = helpUserRepository.existsByEmail(registerMember.getEmail());
        if (flag) {
            throw new BaseException(MemberErrorCode.DUPLICATE_MEMBER);
        }
    }

    public MemberResponseDto.TokenInfo refreshAccessToken(HelpUserEntity member) {
        return MemberResponseDto.TokenInfo.builder()
                .accessToken(jwtTokenProvider.generateAccessToken(member.getUserEmail()))
                .refreshToken(member.getUserToken())
                .build();
    }
}

