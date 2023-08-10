package com.help.hyozason_backend.controller.helpuser;

import com.help.hyozason_backend.dto.helpuser.MemberRequestDto;
import com.help.hyozason_backend.dto.helpuser.MemberResponseDto;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.exception.BaseException;
import com.help.hyozason_backend.security.auth.CurrentMember;
import com.help.hyozason_backend.service.helpuser.HelpUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.io.IOException;

@RequiredArgsConstructor
@RestController //@RestController 어노테이션은 사용된 클래스의 모든 메서드에 자동으로 JSON 변환을 적용
@RequestMapping("/")
public class HelpUserController {

    @Autowired
    private final HelpUserService memberService;

    /**
     * [POST] 소셜 회원가입
     */
    @PostMapping("/signin")
    public ResponseEntity<MemberResponseDto.TokenInfo> register(

            @Valid @RequestBody MemberRequestDto.RegisterMember registerMember) {
        return new ResponseEntity<>
                (memberService.registerMember(registerMember), HttpStatus.OK);
    }

    /**
     * [POST] 소셜 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<MemberResponseDto.TokenInfo> socialLogin(
            @Valid @RequestBody MemberRequestDto.SocialLoginToken socialLoginToken) throws IOException, BaseException {
        return new ResponseEntity<>(
                memberService.socialLogin( socialLoginToken), HttpStatus.OK);
    }


    /**
     * [PATCH] 로그인 토큰 갱신
     */
    @PatchMapping("user/refresh")
    public ResponseEntity<MemberResponseDto.TokenInfo> refreshLogin(@CurrentMember HelpUserEntity member) {
        return new ResponseEntity<>(memberService.refreshAccessToken(member), HttpStatus.OK);
    }


    @GetMapping("/access")
    public ResponseEntity<String> accessToken() {
        // 처리 로직
        return ResponseEntity.ok("Access granted!");
    }




}
