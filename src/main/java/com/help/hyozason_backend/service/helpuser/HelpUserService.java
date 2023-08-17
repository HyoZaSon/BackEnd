
package com.help.hyozason_backend.service.helpuser;


import com.help.hyozason_backend.dto.helpuser.HelpUserDTO;
import com.help.hyozason_backend.dto.helpuser.MemberRequestDto;
import com.help.hyozason_backend.dto.helpuser.MemberResponseDto;
import com.help.hyozason_backend.entity.helpregion.HelpRegionEntity;
import com.help.hyozason_backend.entity.helpreward.HelpRewardEntity;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;

import com.help.hyozason_backend.exception.BaseException;

import com.help.hyozason_backend.exception.MemberErrorCode;
import com.help.hyozason_backend.repository.helpregion.HelpRegionRepository;
import com.help.hyozason_backend.repository.helpreward.HelpRewardRepository;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;
import com.help.hyozason_backend.security.jwt.JwtTokenProvider;
import com.help.hyozason_backend.security.redis.RedisUtil;
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

    private final RedisUtil redisUtil;

    private final HelpRegionRepository regionRepository;
    private final HelpRewardRepository rewardRepository;

    private HelpUserDTO helpUserDTO = new HelpUserDTO();

    private HelpRegionEntity helpRegionEntity = new HelpRegionEntity();

    private HelpUserEntity helpUserEntity = new HelpUserEntity();

    private HelpRewardEntity helpRewardEntity = new HelpRewardEntity();


    public void save(HelpUserEntity member) {
        helpUserRepository.save(member);
    }
    public void saveRegion(HelpRegionEntity region){
        regionRepository.save(region);
    }


    public void saveReward(HelpRewardEntity reward){
        rewardRepository.save(reward);
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
        HelpRegionEntity helpRegionEntity = new HelpRegionEntity(); // 객체를 초기화
        helpRegionEntity.setUserEmail(registerMember.getUserEmail());
        helpRegionEntity.setRegionInfo1(registerMember.getRegionInfo1());
        helpRegionEntity.setRegionInfo2(registerMember.getRegionInfo2());
        saveRegion(helpRegionEntity); // 저장ㅍ
    }

    public void registerReward(MemberRequestDto.RegisterMember registerMember ){
        System.out.println(helpUserDTO); //여기서 null 값이 들어가이ㅗ
        if (helpUserDTO.getUserEmail().equals(registerMember.getUserEmail())) {
            helpRewardEntity.setUserEmail(registerMember.getUserEmail());
            helpRewardEntity.setRewardScore(0);
            saveReward(helpRewardEntity);
        }
    }

    public MemberResponseDto.TokenInfo registerMember(  MemberRequestDto.RegisterMember registerMember  ) {
        checkRegister(registerMember); //DB에서 이메일 비교

        if (helpUserDTO != null && helpUserDTO.getUserEmail() != null &&
                helpUserDTO.getUserEmail().equals(registerMember.getUserEmail())) {
            HelpUserEntity member = register(registerMember);
            MemberResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getUserEmail());
            member.setUserToken(tokenInfo.getRefreshToken());


            save(member);
            registerRegion(registerMember);
            registerReward(registerMember);
            return tokenInfo;
        } else {
            BaseException exception = new BaseException(MemberErrorCode.INCORRECT_INFO);
            throw exception;
        }
    }

    //요청시  Id Token 넣어서 와야함
    public MemberResponseDto.LoginInfo socialLogin(
                                                   MemberRequestDto.SocialLoginToken socialLoginToken) throws BaseException, IOException {
        String idToken = socialLoginToken.getIdToken();
        HelpUserDTO newHelpUserDTO = helpOauthService.getKaKaoEmail(helpOauthService.getKaKaoAccessToken(idToken), new HelpUserDTO());

        helpUserDTO.setUserEmail(newHelpUserDTO.getUserEmail());
        helpUserDTO.setUserName(newHelpUserDTO.getUserName());


        HelpUserEntity member = helpUserRepository.findByUserEmail(newHelpUserDTO.getUserEmail());

        if ( member != null) {
            //토큰 설정
            MemberResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getUserEmail());
            member.changeRefreshToken(tokenInfo.getRefreshToken());
            save(member);


                if ("HELPER".equals(member.getUserRole())) {
                    HelpRegionEntity region =regionRepository.findByUserEmail(helpUserDTO.getUserEmail());
                    HelpRewardEntity reward = rewardRepository.findByUserEmail(helpUserDTO.getUserEmail());
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






