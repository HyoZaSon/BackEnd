package com.help.hyozason_backend.service.helpuser;


import com.help.hyozason_backend.dto.helpuser.HelpUserDTO;
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


    private HelpUserDTO helpUserDTO = new HelpUserDTO();



    public void save(HelpUserEntity member) {
        helpUserRepository.save(member);
    }

    public HelpUserEntity register( MemberRequestDto.RegisterMember registerMember) {
        HelpUserEntity member = new HelpUserEntity();
        if (helpUserDTO.getUserEmail().equals(registerMember.getUserEmail())) {
            // 이메일이 유효하지 않은 경우 에러 처리
         member.setRefreshToken( helpUserDTO.getUserToken());
         member.setUserAge(helpUserDTO.getUserAge());
         member.setUserEmail( registerMember.getUserEmail());
         member.setUserGender(helpUserDTO.getUserGender());
         member.setUserName(helpUserDTO.getUserName());
         member.setUserPhone( registerMember.getUserPhone());
         member.setUserRole(registerMember.getUserRole());


        }else{
            BaseException exception = new BaseException(MemberErrorCode.INVALID_MEMBER);
//            exception.setEmailMessage(registerMember.getUserEmail());
            throw exception;
        }
        return member;

    }

    public MemberResponseDto.TokenInfo registerMember(  MemberRequestDto.RegisterMember registerMember  ) {
        checkRegister(registerMember);

        helpUserDTO.setUserEmail(helpUserDTO.getUserEmail());
        helpUserDTO.setUserAge(helpUserDTO.getUserAge());
        helpUserDTO.setUserGender(helpUserDTO.getUserGender());

        HelpUserEntity member = register(registerMember);
        MemberResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getUserEmail());
        member.changeRefreshToken(tokenInfo.getRefreshToken());
        member.setRefreshToken(tokenInfo.getRefreshToken());
        save(member);
        return tokenInfo;
    }

    //요청시 reqeust body에 Id Token 넣어서 와야함
    public MemberResponseDto.TokenInfo socialLogin(
                                                   MemberRequestDto.SocialLoginToken socialLoginToken) throws BaseException, IOException {
        String idToken = socialLoginToken.getIdToken();
        helpUserDTO= helpOauthService.getKaKaoEmail(helpOauthService.getKaKaoAccessToken(idToken),helpUserDTO);
        HelpUserEntity member = helpUserRepository.findByUserEmail(helpUserDTO.getUserEmail());

        if ( member != null) {
            MemberResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getUserEmail());
            member.changeRefreshToken(tokenInfo.getRefreshToken());
            return tokenInfo;
        } else {
            //DB에 이메일이 없을 경우
            BaseException exception = new BaseException(MemberErrorCode.UN_REGISTERED_MEMBER);
            exception.setEmailMessage(helpUserDTO.getUserEmail());
            throw exception;
        }
    }

    public void checkRegister(MemberRequestDto.RegisterMember registerMember) {
        HelpUserEntity member = helpUserRepository.findByUserEmail(registerMember.getUserEmail());
        if(member != null) {
            if (member.getUserEmail().equals(registerMember.getUserEmail())) {
                throw new BaseException(MemberErrorCode.DUPLICATE_MEMBER);
            }
        }
    }

    public MemberResponseDto.TokenInfo refreshAccessToken(HelpUserEntity member) {
        return MemberResponseDto.TokenInfo.builder()
                .accessToken(jwtTokenProvider.generateAccessToken(member.getUserEmail()))
                .refreshToken(member.getRefreshToken())
                .build();
    }
}

