package com.help.hyozason_backend.controller.helprequest;


import com.help.hyozason_backend.dto.helprequest.HelpRequestDTO;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.etc.HelpResponse;
import com.help.hyozason_backend.jwt.JwtTokenProvider;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;
import com.help.hyozason_backend.service.helprequest.HelpRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


//@RequiredArgsConstructor //Lombok으로 스프링에서 DI(의존성 주입)의 방법 중에 생성자 주입을 임의의 코드없이 자동으로 설정해주는 어노테이션
@RestController //@RestController 어노테이션은 사용된 클래스의 모든 메서드에 자동으로 JSON 변환을 적용
@RequestMapping("/help/helprequest")
public class HelpRequestController{

    private final HelpRequestService helpRequestService;
    private final HelpUserRepository helpUserRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public HelpRequestController(HelpRequestService helpRequestService, HelpUserRepository helpUserRepository, JwtTokenProvider jwtTokenProvider, SimpMessageSendingOperations messagingTemplate) {
        this.helpRequestService = helpRequestService;
        this.helpUserRepository = helpUserRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.messagingTemplate = messagingTemplate;
    }


    /***도움 요청 소켓 통신 활용 관련 코드***/
    //사용자 A 도움 요청
    @MessageMapping("/requestHelp") // "/app/requestHelp" 로 접근해야함.
    @SendTo("/topic/request") // 처리를 마친 후 결과메시지를 설정한 경로 - 구독시에 구독자들에게 알림이 간다.
    public HelpResponse requestHelp(Message<HelpRequestDTO> message, StompHeaderAccessor stompHeaderAccessor) throws InterruptedException {
        Thread.sleep(1000);

        // 메시지의 내용물 가져오기
        HelpRequestDTO helpRequestDTO = message.getPayload();
        //토큰 값을 헤더에서 추출
        List<String> tokenHeaders = stompHeaderAccessor.wrap(message).getNativeHeader("Authorization");
        String token = tokenHeaders != null && !tokenHeaders.isEmpty() ? tokenHeaders.get(0) : null;
        //회원 정보 추출
        String userEmail = jwtTokenProvider.getUserPk(token);

        //글 작성 service 메소드 호출, 고유한 helpId return
        long helpRequestId =  helpRequestService.writeHelpRequest(helpRequestDTO, userEmail);
        // 도움 요청이 생성되었을 때, 해당 도움 요청의 고유 ID를 추출하고, 이를 활용하여 도움 요청에 대한 주제 생성
        String topicPath = "/topic/request/" + helpRequestId; // 처리를 마친 후 결과메시지를 설정한 경로 - 구독시에 구독자들에게 알림이 간다.

        //글 작성 이후에 구독자 (사용자 A) 에게 도움 요청 상태(글 업로드 됨)를 전달한다.
        messagingTemplate.convertAndSend(topicPath,helpRequestDTO);

        //HelpUserEntity helpUserEntity = helpUserRepository.findBy(helpRequestDTO.getUserId());
        return null;
    }

    //B 요청 수락
    @MessageMapping("/acceptHelp/{helpRequestId}")
    @SendToUser("/queue/acceptHelp")
    public HelpResponse acceptHelp(@DestinationVariable Long helpRequestId, Message<HelpRequestDTO> message, StompHeaderAccessor stompHeaderAccessor){
        //log.debug("Received message: {}",message);
        //HelpBoard의 helpAccept 값을 true로 변경 service 메소드 사용
        //HelpRequestDTO helpRequestDTO = helpRequestService.acceptHelp(message);

        //토큰 값을 헤더에서 추출
        List<String> tokenHeaders = stompHeaderAccessor.wrap(message).getNativeHeader("Authorization");
        String token = tokenHeaders != null && !tokenHeaders.isEmpty() ? tokenHeaders.get(0) : null;
        //회원 정보 추출
        String userEmail = jwtTokenProvider.getUserPk(token);
        HelpUserEntity helpUserEntity = helpUserRepository.findByEmail(userEmail);

        //서비스를 통해서 helpAccept 여부 변경
        helpRequestService.acceptHelpRequest(helpRequestId);

        // 도움 요청 수락 처리 후 사용자 A에게 알림을 보내기
        // 사용자 A의 구독 경로: /topic/request/{helpRequestId}
        helpRequestService.notifyUserHelpAccepted(helpRequestId, helpUserEntity);

        return null;
    }


}
