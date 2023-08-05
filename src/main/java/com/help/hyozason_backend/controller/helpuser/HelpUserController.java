package com.help.hyozason_backend.controller.helpuser;

import com.help.hyozason_backend.dto.helpuser.HelpUserDTO;
import com.help.hyozason_backend.service.helpuser.HelpUserService;
import org.springframework.web.bind.annotation.*;

//@RequiredArgsConstructor //Lombok으로 스프링에서 DI(의존성 주입)의 방법 중에 생성자 주입을 임의의 코드없이 자동으로 설정해주는 어노테이션
@RestController //@RestController 어노테이션은 사용된 클래스의 모든 메서드에 자동으로 JSON 변환을 적용
@RequestMapping("/help")
public class HelpUserController {

//    private final HelpUserService helpUserService;
    @PostMapping("/sign-up")
    public String signUp(@RequestBody HelpUserDTO helpUserDTO) throws Exception {
//        helpUserService.signUp(helpUserDTO);
        return "회원가입 성공";
    }

    @GetMapping("/jwt-test")
    public String jwtTest() {
        return "jwtTest 요청 성공";
    }
}
