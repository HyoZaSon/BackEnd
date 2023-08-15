package com.help.hyozason_backend.controller.helpreward;

import com.help.hyozason_backend.controller.jwt.JwtController;
import com.help.hyozason_backend.service.helpreward.HelpRewardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//@RequiredArgsConstructor //Lombok으로 스프링에서 DI(의존성 주입)의 방법 중에 생성자 주입을 임의의 코드없이 자동으로 설정해주는 어노테이션
@RestController //@RestController 어노테이션은 사용된 클래스의 모든 메서드에 자동으로 JSON 변환을 적용
@RequestMapping("/help")
public class HelpRewardController {
    private HelpRewardService helpRewardService;
    private final JwtController jwtController;

    public HelpRewardController(HelpRewardService helpRewardService, JwtController jwtController) {
        this.helpRewardService = helpRewardService;
        this.jwtController = jwtController;
    }

    /*@PostMapping("/reward")
    public ResponseEntity<Object> updateRewards(@RequestBody Map<String, Object> request) {
        try {
            int rating = (int) request.get("rating");
            String userId = (String) request.get("userId");
            int updatedPoints = helpRewardService.updateRewards(userId, rating);

            return ResponseEntity.ok(updatedPoints);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("잘못된 입력입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류입니다.");
        }
    }*/

    @GetMapping("/reward")
    public ResponseEntity<Object> updateRewards(
            @RequestParam(name = "rating") int rating,
            HttpServletRequest request
    ) {
        try {
            String userEmail = jwtController.getUserEmail(request);
            if (userEmail == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            int updatedPoints = helpRewardService.updateRewards(userEmail, rating);
            return ResponseEntity.ok(updatedPoints);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("잘못된 입력입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류입니다.");
        }
    }
}
