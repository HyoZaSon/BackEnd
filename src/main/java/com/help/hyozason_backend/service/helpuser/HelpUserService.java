package com.help.hyozason_backend.service.helpuser;

import com.help.hyozason_backend.dto.helpreward.HelpRewardDTO;
import com.help.hyozason_backend.dto.helpuser.HelpUserDTO;
import com.help.hyozason_backend.dto.helpuser.MemberRequestDto;
import com.help.hyozason_backend.dto.helpuser.MemberRequestDto.RegisterMember;
import com.help.hyozason_backend.dto.helpuser.MemberResponseDto;
import com.help.hyozason_backend.dto.helpuser.MemberResponseDto.TokenInfo;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.etc.ResponseService;
import com.help.hyozason_backend.exception.BaseException;
import com.help.hyozason_backend.exception.MemberErrorCode;
import com.help.hyozason_backend.mapper.helpuser.HelpUserMapper;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;
import com.help.hyozason_backend.security.jwt.JwtTokenProvider;
import com.help.hyozason_backend.security.oauth.SocialLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.config.oauth2.client.CommonOAuth2Provider.GOOGLE;

@Service
public class HelpUserService extends ResponseService {


    private final SocialLoginService socialLoginService;
    private final HelpUserRepository helpUserRepository;
    private final JwtTokenProvider jwtTokenProvider;


    public void save(HelpUserDTO member) {
        HelpUserRepository.save(member);
    }

    public HelpUserEntity register(RegisterMember registerMember) {
        HelpUserDTO member = new HelpUserEntity(
                registerMember.getEmail(),
                save(HelpUserMapper.INSTANCE.toEntity(member));
                //DTO -> Entity 바꾸는 로직 필요
        return member;
    }

    public TokenInfo registerMember(RegisterMember registerMember) {
        checkRegister(registerMember);
        HelpUserEntity member = register(registerMember);
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getUserId());
        member.changeRefreshToken(tokenInfo.getRefreshToken());
        return tokenInfo;
    }

    public TokenInfo socialLogin(MemberRequestDto.SocialLoginToken socialLoginToken) throws BaseException, IOException {
        String idToken = socialLoginToken.getIdToken();
        String email = "";

         const email = socialLoginService.getKaKaoEmail(socialLoginService.getKaKaoAccessToken(idToken));



        HelpUserEntity member = HelpUserRepository.findByEmail(email).orElse(null);
        if (member != null) {
            TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getUserId());
            member.changeRefreshToken(tokenInfo.getRefreshToken());
            return tokenInfo;
        } else {
            BaseException exception = new BaseException(MemberErrorCode.UN_REGISTERED_MEMBER);
            exception.setEmailMessage(email);
            throw exception;
        }
    }

    public void checkRegister(RegisterMember registerMember) {
        Boolean flag = memberRepository.existsByEmail(registerMember.getEmail());
        if (flag) {
            throw new BaseException(MemberErrorCode.DUPLICATE_MEMBER);
        }
        flag = memberRepository.existsByNickName(registerMember.getEmail());
        if (flag) {
            throw new BaseException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

//    public MemberResponseDto.CheckNickNameRes checkNickName(CheckNickName checkNickName) {
//        return new MemberResponseDto.CheckNickNameRes(
//                memberRepository.existsByNickName(checkNickName.getNickName()));
//    }


    public TokenInfo refreshAccessToken(HelpUserEntity member) {
        return TokenInfo.builder()
                .accessToken(jwtTokenProvider.generateAccessToken(member.getUserId()))
                .refreshToken(member.getRefreshToken())
                .build();
    }
}

