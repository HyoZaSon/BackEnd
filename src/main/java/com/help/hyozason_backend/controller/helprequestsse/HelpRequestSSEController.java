package com.help.hyozason_backend.controller.helprequestsse;


import com.help.hyozason_backend.controller.jwt.JwtController;
import com.help.hyozason_backend.dto.helprequest.HelpRequestDTO;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.etc.HelpResponse;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;
import com.help.hyozason_backend.service.helprequest.HelpRequestService;
import com.help.hyozason_backend.service.helprequestsse.HelpRequestSSEService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

//@RequiredArgsConstructor //Lombok으로 스프링에서 DI(의존성 주입)의 방법 중에 생성자 주입을 임의의 코드없이 자동으로 설정해주는 어노테이션
@RestController //@RestController 어노테이션은 사용된 클래스의 모든 메서드에 자동으로 JSON 변환을 적용
@RequestMapping("/help/helprequest")
@Slf4j
public class HelpRequestSSEController {
    private final SseEmitters sseEmitters;
    private final HelpRequestSSEService helpRequestSSEService;
    private final JwtController jwtController;
    private final HelpRequestService helpRequestService;
    private final HelpUserRepository helpUserRepository;
    public HelpRequestSSEController(SseEmitters sseEmitters, HelpRequestSSEService helpRequestSSEService, JwtController jwtController, HelpRequestService helpRequestService, HelpUserRepository helpUserRepository) {
        this.sseEmitters = sseEmitters;
        this.helpRequestSSEService = helpRequestSSEService;
        this.jwtController = jwtController;

        this.helpRequestService = helpRequestService;
        this.helpUserRepository = helpUserRepository;
    }

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity connect(@RequestHeader(value = "Authorization", required = false, defaultValue = "")HttpRequest request){
        SseEmitter emitter = new SseEmitter(); //생성자를 통해 만료시간을 설정할 수 있습니다. 기본 30분,  SseEmitter emitter = new SseEmitter(60 * 1000L);
        sseEmitters.add(emitter); //생성된 SseEmitter 객체는 향후 이벤트가 발생했을 때, 해당 클라이언트로 이벤트를 전송하기 위해 사용되므로 서버에서 저장
        try {
            /**
             *  Emitter를 생성하고 나서 만료 시간까지 아무런 데이터도 보내지 않으면 재연결 요청시 503 Service Unavailable 에러가 발생할 수 있습니다.
             *  따라서 처음 SSE 연결 시 더미 데이터를 전달해주는 것이 안전합니다.
             * */
            emitter.send(SseEmitter.event()
                    .name("connect")  // 해당 이벤트의 이름 지정
                    .data("connected!"));// 503 에러 방지를 위한 더미 데이터
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }

    @PostMapping("/count")
    public ResponseEntity<Void> count() {
        sseEmitters.count();
        return ResponseEntity.ok().build();
    }



    @PostMapping("/requestHelp")
    public ResponseEntity<HelpResponse> requestHelp(@RequestBody HelpRequestDTO helpRequestDTO, HttpServletRequest request) throws Exception {
        try {
            String userEmail = jwtController.getUserEmail(request);
            if (userEmail != null && !userEmail.isEmpty()) {
                Long helpBoardId = helpRequestSSEService.writeHelpRequest(helpRequestDTO, userEmail);

                // 생성된 도움 요청 정보를 클라이언트로 전송
                return ResponseEntity.ok((HelpResponse) Map.of("message","help request board upload success","helpBoardId", helpBoardId));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * 요청 수락
     * */
    @PostMapping("/acceptHelp/{helpBoardId}")
    public ResponseEntity<HelpResponse> acceptHelpRequest(@PathVariable Long helpBoardId, HttpServletRequest request) {
        try {
            //B 회원 정보 추출
            String userEmail = jwtController.getUserEmail(request);
            HelpUserEntity helperUserEntity = helpUserRepository.findByUserEmail(userEmail);

            if (userEmail != null && helperUserEntity != null) {
                String helpEmail = helpRequestSSEService.acceptHelpRequest(helpBoardId);
                if (helpEmail != null) {
                    helpRequestSSEService.notifyUserHelpAccepted(helpBoardId,helperUserEntity,helpEmail);
                    return ResponseEntity.ok((HelpResponse) Map.of("message", "도움 요청이 수락되었습니다", "helpBoardId", helpBoardId));

                } else {
                    helpRequestSSEService.notifyUserHelpDenided(helpBoardId, helperUserEntity);

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((HelpResponse) Map.of("message", "Help request already accepted or not found.", "helpBoardId",helpBoardId));
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
