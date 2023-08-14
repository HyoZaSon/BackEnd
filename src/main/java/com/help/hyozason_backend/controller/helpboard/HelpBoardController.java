package com.help.hyozason_backend.controller.helpboard;

import com.help.hyozason_backend.controller.jwt.JwtController;
import com.help.hyozason_backend.dto.helpboard.HelpBoardDTO;
import com.help.hyozason_backend.service.helpboard.HelpBoardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@RequiredArgsConstructor //Lombok으로 스프링에서 DI(의존성 주입)의 방법 중에 생성자 주입을 임의의 코드없이 자동으로 설정해주는 어노테이션
@RestController //@RestController 어노테이션은 사용된 클래스의 모든 메서드에 자동으로 JSON 변환을 적용
@RequestMapping("/help")
public class HelpBoardController {
    private final HelpBoardService helpBoardService;
    private JwtController jwtController;


    @Autowired
    public HelpBoardController(HelpBoardService helpBoardService, JwtController jwtController) {
        this.helpBoardService = helpBoardService;
        this.jwtController = jwtController;
    }

    @GetMapping("/read")
    public ResponseEntity<List<HelpBoardDTO>> getHelpBoards(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(name = "region_2depth_name") String region2DepthName,
            HttpServletRequest request
    ) {
        try {
            String userEmail = jwtController.getUserEmail(request);
            if (userEmail != null && !userEmail.isEmpty()) {
                // 로그인된 경우
                return helpBoardService.getHelpBoards(pageable, region2DepthName);
            } else {
                // 로그인되지 않은 경우
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

