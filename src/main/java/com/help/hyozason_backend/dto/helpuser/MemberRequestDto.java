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
        private String userEmail;
        private String userPhone;
        private String userRole;

    }



}
