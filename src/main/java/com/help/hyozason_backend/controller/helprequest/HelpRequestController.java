package com.help.hyozason_backend.controller.helprequest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.help.hyozason_backend.controller.jwt.JwtController;
import com.help.hyozason_backend.dto.helprequest.HelpRequestDTO;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.etc.HelpResponse;
import com.help.hyozason_backend.etc.ResponseService;

import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;
import com.help.hyozason_backend.security.jwt.JwtTokenProvider;
import com.help.hyozason_backend.service.helprequest.HelpRequestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


//@RequiredArgsConstructor //Lombok으로 스프링에서 DI(의존성 주입)의 방법 중에 생성자 주입을 임의의 코드없이 자동으로 설정해주는 어노테이션
@RestController //@RestController 어노테이션은 사용된 클래스의 모든 메서드에 자동으로 JSON 변환을 적용
@RequestMapping("/help/helprequest")
public class HelpRequestController extends ResponseService {

    private final HelpRequestService helpRequestService;
    private final HelpUserRepository helpUserRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public HelpRequestController(HelpRequestService helpRequestService, HelpUserRepository helpUserRepository, JwtTokenProvider jwtTokenProvider,SimpMessageSendingOperations messagingTemplate) {
        this.helpRequestService = helpRequestService;
        this.helpUserRepository = helpUserRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/hello")
    public void message(Message message){
        messagingTemplate.convertAndSend("/topic/hello",message);
    }


    /***도움 요청 소켓 통신 활용 관련 코드***/
    //사용자 A 도움 요청
    @MessageMapping("/requestHelp") // "/app/requestHelp" 로 접근해야함.
    @SendTo("/topic/request") // 처리를 마친 후 결과메시지를 설정한 경로 - 구독시에 구독자들에게 알림이 간다.
    public HelpResponse requestHelp(Message<String> message, StompHeaderAccessor stompHeaderAccessor) throws Exception {
        Thread.sleep(1000);

        // 메시지의 내용물 가져오기
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = message.getPayload();

        HelpRequestDTO helpRequestDTO = objectMapper.readValue(jsonMessage, HelpRequestDTO.class);

        System.out.println("메시지 :" + helpRequestDTO);


        //토큰 값을 헤더에서 추출
        List<String> tokenHeaders = stompHeaderAccessor.wrap(message).getNativeHeader("Authorization");
        String token = tokenHeaders != null && !tokenHeaders.isEmpty() ? tokenHeaders.get(0) : null;
        //회원 정보 추출
        String userEmail = jwtTokenProvider.getUserPk(token);

        //글 작성 service 메소드 호출, 고유한 helpId return
        long helpBoardId = helpRequestService.writeHelpRequest(helpRequestDTO, userEmail);

        // 도움 요청이 생성되었을 때, 해당 도움 요청의 고유 ID를 추출하고, 이를 활용하여 도움 요청에 대한 주제 생성
        String topicPath = "/topic/request"; // 처리를 마친 후 결과메시지를 설정한 경로 - 구독시에 구독자들에게 알림이 간다.

        //글 작성 이후에 구독자 (사용자 A) 에게 도움 요청 상태(글 업로드 됨)를 전달한다.
        messagingTemplate.convertAndSend(topicPath,"help request board upload success. 게시글 번호 : "+helpBoardId+"사용자 이메일 :"+userEmail);

        return setResponse(200,"help request board upload success",helpBoardId);
    }

    //B 요청 수락
    @MessageMapping("/acceptHelp/{helpBoardId}")
    @SendToUser("/queue/acceptHelp")
    public HelpResponse acceptHelp(@DestinationVariable Long helpBoardId, Message<String> message, StompHeaderAccessor stompHeaderAccessor) throws Exception {
        //log.debug("Received message: {}",message);

        //토큰 값을 헤더에서 추출
        List<String> tokenHeaders = stompHeaderAccessor.wrap(message).getNativeHeader("Authorization");
        String token = tokenHeaders != null && !tokenHeaders.isEmpty() ? tokenHeaders.get(0) : null;
        //B 회원 정보 추출
        String userEmail = jwtTokenProvider.getUserPk(token);
//        Optional <HelpUserEntity> helperUserEntity = helpUserRepository.findByUserEmail(userEmail);
        HelpUserEntity helperUserEntity = helpUserRepository.findByUserEmail(userEmail);

        //서비스를 통해서 helpAccept 여부 변경
        String helpEmail =  helpRequestService.acceptHelpRequest(helpBoardId);

        //helpEmail 이 null일 경우
        if(helpEmail == null){
            messagingTemplate.convertAndSendToUser(userEmail,"/queue/acceptHelp/"+helpBoardId,"이미 다른 사람이 수락했습니다.");
            return setResponse(400,"이미 다른 사람이 수락했습니다.",null);
        }else{
            // 도움 요청 수락 처리 후 사용자 A에게 알림을 보내기
            // 사용자 A의 구독 경로: /topic/request/{helpBoardId}
            helpRequestService.notifyUserHelpAccepted(helpBoardId,helperUserEntity,helpEmail);
            return setResponse(200,"도움 요청 수락 성공",helpEmail);
        }
    }


    /*@PostMapping("/write")
    public ResponseEntity<Long> writeHelpRequest(@RequestBody HelpRequestDTO helpRequestDTO, HttpServletRequest request) throws Exception {
        try {
            String userEmail = jwtController.getUserEmail(request);
            if(userEmail != null && !userEmail.isEmpty()) {
                Long result = helpRequestService.writeHelpRequest(helpRequestDTO, userEmail);
                return ResponseEntity.ok(result);
            }
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/accept/{helpBoardId}")
    public ResponseEntity<String> acceptHelpRequest(@PathVariable Long helpBoardId, @RequestBody HelpUserEntity helperUserEntity, HttpServletRequest request) {
        try {
            String userEmail = jwtController.getUserEmail(request);
            if (userEmail != null && !userEmail.isEmpty()) {
                String result = helpRequestService.acceptHelpRequest(helpBoardId, helperUserEntity);
                if (result != null) {
                    return ResponseEntity.ok("Help request accepted and notifications sent.");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Help request already accepted or not found.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }*/


}
