package com.help.hyozason_backend.dto.helpuser;



import com.help.hyozason_backend.dto.helpregion.HelpRegionDTO;
import com.help.hyozason_backend.dto.helpreward.HelpRewardDTO;
import com.help.hyozason_backend.entity.helpregion.HelpRegionEntity;
import com.help.hyozason_backend.entity.helpreward.HelpRewardEntity;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


public class MemberResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TokenInfo {

        private String accessToken;
        private String refreshToken;
    }

    // 공통으로 리턴해주는 부분
    @Getter
    @AllArgsConstructor
    public static class LoginInfo{
        private TokenInfo tokenInfo;
        private String userRole;
        private String nickName;

        private String regionInfo1; //~구
        private long rewardScore; //리워드


        public static LoginInfo helpLogin(TokenInfo tokenInfo, String userRole, String nickName) {
            return new LoginInfo(tokenInfo, userRole, nickName, null, 0);
        }

        public static LoginInfo helperLogin(TokenInfo tokenInfo, String userRole, String nickName, String regionInfo1, long rewardScore) {
            return new LoginInfo(tokenInfo, userRole, nickName, regionInfo1, rewardScore);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class HelperLogin {

        private LoginInfo loginInfo;
        private String regionInfo2; //~구
        private long rewardScore; //리워드


    }





}