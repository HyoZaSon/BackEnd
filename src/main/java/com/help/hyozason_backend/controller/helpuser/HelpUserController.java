package com.help.hyozason_backend.controller.helpuser;

import com.help.hyozason_backend.dto.helpuser.MemberRequestDto;
import com.help.hyozason_backend.dto.helpuser.MemberResponseDto;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.security.auth.CurrentMember;
import com.help.hyozason_backend.service.helpuser.HelpUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

//@RequiredArgsConstructor //Lombok으로 스프링에서 DI(의존성 주입)의 방법 중에 생성자 주입을 임의의 코드없이 자동으로 설정해주는 어노테이션
@RestController //@RestController 어노테이션은 사용된 클래스의 모든 메서드에 자동으로 JSON 변환을 적용
@RequestMapping("/help")
public class HelpUserController {

    private final HelpUserService memberService;

    /**
     * [POST] 소셜 회원가입
     */
    @PostMapping("/sign-up")
    public ResponseEntity<MemberResponseDto.TokenInfo> register(
            @Valid @RequestBody MemberRequestDto.RegisterMember registerMember) {
        return new ResponseEntity<>
                (memberService.registerMember(registerMember), HttpStatus.OK);
    }

    /**
     * [POST] 소셜 로그인
     */
    @PostMapping("/{socialLoginType}/login")
    public ResponseEntity<MemberResponseDto.TokenInfo> socialLogin(
            @Valid @RequestBody MemberRequestDto.SocialLoginToken socialLoginToken) throws IOException {
        return new ResponseEntity<>(
                memberService.socialLogin( socialLoginToken), HttpStatus.OK);
    }

    /**
     * [POST] 닉네임 중복 확인
     */
//    @PostMapping("/nick-name")
//    public ResponseEntity<MemberResponseDto.CheckNickNameRes> checkNickName(
//            @Valid @RequestBody CheckNickName checkNickName) {
//        return new ResponseEntity<>(memberService.checkNickName(checkNickName),
//                HttpStatus.OK);
//    }


    /**
     * [PATCH] 프로필 수정
     */
//    @PatchMapping("/member/profile")
//    public ResponseEntity<String> modifyProfile(
//            @CurrentMember Member member, @RequestPart(value = "modifyProfile") ModifyProfile modifyProfile,
//            @RequestPart(value = "image", required = false) List<MultipartFile> multipartFile) {
//        return new ResponseEntity<>(memberService.modifyProfile(member, modifyProfile, multipartFile), HttpStatus.OK);
//    }

    /**
     * [GET] 프로필 조회
     */
//    @GetMapping("/profile/{id}")
//    public ResponseEntity<MemberProfileInfo> getProfile(
//            @PathVariable("id") Long memberId) {
//        return new ResponseEntity<>(memberService.getProfile(memberId), HttpStatus.OK);
//    }

    /**
     * [PATCH] 로그인 토큰 갱신
     */
    @PatchMapping("/member/refresh")
    public ResponseEntity<MemberResponseDto.TokenInfo> refreshLogin(@CurrentMember HelpUserEntity member) {
        return new ResponseEntity<>(memberService.refreshAccessToken(member), HttpStatus.OK);
    }
}
