package com.help.hyozason_backend.dto.helpuser;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class MemberRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocialLoginToken {
        @NotBlank(message = "ID Token이 없습니다.")
        private String idToken;
    }



    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterMember {
        @NotBlank(message = "이메일 형식이 아닙니다.")
        @Email(message = "이메일 형식이 아닙니다.")
        private String email;
//        @NotBlank(message = "닉네임은 공백일 수 없습니다.")
//        private String nickname;
//
//        @NotBlank(message = "mbti를 골라주세요.")
//        private String caseSensitivity; //대소문자 구분
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ModifyProfile {
        private String nickName;

    }
}
