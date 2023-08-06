package com.help.hyozason_backend.dto.helpuser;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


public class MemberResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TokenInfo {

        private String accessToken;
        private String refreshToken;
    }



}