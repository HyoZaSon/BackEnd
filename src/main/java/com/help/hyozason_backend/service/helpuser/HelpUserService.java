package com.help.hyozason_backend.service.helpuser;


import com.help.hyozason_backend.dto.helpregion.HelpRegionDTO;
import com.help.hyozason_backend.dto.helpuser.HelpUserDTO;
import com.help.hyozason_backend.dto.helpuser.MemberRequestDto;
import com.help.hyozason_backend.dto.helpuser.MemberResponseDto;
import com.help.hyozason_backend.entity.helpregion.HelpRegionEntity;
import com.help.hyozason_backend.entity.helpreward.HelpRewardEntity;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;

import com.help.hyozason_backend.exception.AuthErrorCode;
import com.help.hyozason_backend.exception.BaseException;
import com.help.hyozason_backend.exception.MemberErrorCode;
import com.help.hyozason_backend.repository.helpregion.HelpRegionRepository;
import com.help.hyozason_backend.repository.helpreward.HelpRewardRepository;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;
import com.help.hyozason_backend.security.jwt.JwtTokenProvider;
import com.help.hyozason_backend.security.redis.RedisUtil;
import com.help.hyozason_backend.service.helpoauth.HelpOauthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class HelpUserService  {

    private final JwtTokenProvider jwtTokenProvider;

    private final HelpOauthService helpOauthService;
    private final HelpUserRepository helpUserRepository;

    private final RedisUtil redisUtil;

    private final HelpRegionRepository regionRepository;
    private final HelpRewardRepository rewardRepository;

    private HelpUserDTO helpUserDTO = new HelpUserDTO();

    private HelpRegionEntity helpRegionEntity = new HelpRegionEntity();

    private HelpUserEntity helpUserEntity = new HelpUserEntity();



    public void save(HelpUserEntity member) {
        helpUserRepository.save(member);
    }
    public void saveRegion(HelpRegionEntity region){
        regionRepository.save(region);
    }

    public HelpUserEntity register( MemberRequestDto.RegisterMember registerMember ) {
        helpUserEntity.setUserToken( helpUserDTO.getUserToken());
        helpUserEntity.setUserEmail( registerMember.getUserEmail());
        helpUserEntity.setUserName(helpUserDTO.getUserName());
        helpUserEntity.setUserPhone( registerMember.getUserPhone());
        helpUserEntity.setUserRole(registerMember.getUserRole());
        return helpUserEntity;
    }

    public void registerRegion( MemberRequestDto.RegisterMember registerMember ) {
        helpRegionEntity.setRegionInfo1(registerMember.getRegionInfo1());
        helpRegionEntity.setRegionInfo2(registerMember.getRegionInfo2());
        helpRegionEntity.setUserEmail(helpUserDTO.getUserEmail());
        saveRegion(helpRegionEntity);

    }

    public MemberResponseDto.TokenInfo registerMember(  MemberRequestDto.RegisterMember registerMember  ) {
        checkRegister(registerMember); //DB에서 이메일 비교
        if (helpUserDTO.getUserEmail().equals(registerMember.getUserEmail())) {
            HelpUserEntity member = register(registerMember);
            MemberResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getUserEmail()); //토큰 발급
            member.setUserToken(tokenInfo.getRefreshToken()); //refreshToekn Entity 에 set
            save(member); //member 저장 -> 토큰 때문에 재저장
            //프론트 측에서 주소 입력 받을 경우에만 회원가입 하도록 설정해야함 -> 여기서 에러 처리 어려움...
             registerRegion(registerMember);
            return tokenInfo;
        }else{
            BaseException exception = new BaseException(MemberErrorCode.INVALID_MEMBER);
            throw exception;
        }
    }

    //요청시  Id Token 넣어서 와야함
    public MemberResponseDto.LoginInfo socialLogin(
                                                   MemberRequestDto.SocialLoginToken socialLoginToken) throws BaseException, IOException {
        String idToken = socialLoginToken.getIdToken();
        helpUserDTO= helpOauthService.getKaKaoEmail(helpOauthService.getKaKaoAccessToken(idToken),helpUserDTO);
        HelpUserEntity member = helpUserRepository.findByUserEmail(helpUserDTO.getUserEmail());
        HelpRegionEntity region =regionRepository.findByUserEmail(helpUserDTO.getUserEmail());
        HelpRewardEntity reward = rewardRepository.findByUserEmail(helpUserDTO.getUserEmail());

        if ( member != null) {
            //토큰 설정
            MemberResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getUserEmail());
            member.changeRefreshToken(tokenInfo.getRefreshToken());
            save(member);


                if ("HELPER".equals(member.getUserRole())) {
                    MemberResponseDto.LoginInfo  helperInfo = MemberResponseDto.LoginInfo.helperLogin(tokenInfo, member.getUserRole(), member.getUserName(), region.getRegionInfo1(), reward.getRewardScore());
                    return helperInfo;
                }
            MemberResponseDto.LoginInfo helpInfo = MemberResponseDto.LoginInfo.helpLogin(tokenInfo, member.getUserRole(), member.getUserName());
            return helpInfo;

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
        System.out.print(member.getUserEmail());
        System.out.print(member);
        return MemberResponseDto.TokenInfo.builder()
                .accessToken(jwtTokenProvider.generateAccessToken(member.getUserEmail()))
                .refreshToken(member.getUserToken())
                .build();
    }

    public void Logout (String accessToken ){
        System.out.println("로그아웃 시작");

        //accessToken 이용해  이메일 가져옴
        String userEmail = jwtTokenProvider.getMemberIdByToken(accessToken);
        System.out.println(userEmail);

        //이메일로 데이터 베이스에서 유저 찾아서 유저 토큰 제거

       HelpUserEntity helpUser = helpUserRepository.findByUserEmail(userEmail);

           if (helpUser.getUserToken() != null && isTokenBlacklisted(accessToken)==false) {
               helpUser.setUserToken(null);
               save(helpUser);

               //Access Token blacklist에 등록하여 만료시키기
               //남은 유효시간 얻음
               Long expiration = jwtTokenProvider.getExpiration(accessToken);
               redisUtil.setBlackList(accessToken,"access_token", expiration);
               System.out.println("블랙리스트 등록 ");
           }
       else {
        BaseException exception = new BaseException(MemberErrorCode.ALREADY_LOGOUT);
        throw exception;
     }
       }

        // 블랙리스트에 accessToken 이  있는지 확인하는 메서드
       public boolean isTokenBlacklisted(String token){
        return redisUtil.hasKeyBlackList(token);
       }






    }






