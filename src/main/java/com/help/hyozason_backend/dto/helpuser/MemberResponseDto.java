package com.help.hyozason_backend.dto.helpuser;


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

//    @Getter
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class MemberSimpleInfo {
//
//        private Long id;
//        private String nickName;
//        private String mbti;
//        private String badge;
//        private String profileImgUrl;
//
//        public MemberSimpleInfo(Member member, String badge){
//            this.id = member.getId();
//            this.nickName = member.getNickName();
//            this.mbti = member.getDetailMbti();
//            this.badge = badge;
//            this.profileImgUrl = member.getProfileImageUrl();
//        }
//    }

    @Getter
    @AllArgsConstructor
    public static class CheckNickNameRes {

        private boolean isUsed;
    }

    @Getter
    @AllArgsConstructor
    public static class HelperInfo {

        private Long id;
        private String nickName;
        private String mbti;
        private String badge;
        private String profileImgUrl;
        private String introduction;

        public HelperInfo(HelpUserEntity member) {
            this.id = member.getUserId();
            this.nickName = member.getNickName();
            this.profileImgUrl = member.getProfileImageUrl();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class HelpInfo {
        private HelpInfo teacherInfo;

//        private EvaluationCount evaluationCount;
//        private BoardHistory boardHistory;
//        private DiscussionHistory discussionHistory;
//        private WorryBoardHistory worryBoardHistory;
    }
}