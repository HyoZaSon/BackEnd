package com.help.hyozason_backend.helprequesttest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.help.hyozason_backend.dto.helprequest.HelpRequestDTO;
import com.help.hyozason_backend.dto.helpuser.HelpUserDTO;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;
import com.help.hyozason_backend.service.helprequest.HelpRequestService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelpRequestTest {

    static final String WEB_SOCKET_URI = "ws://localhost:8082/ws-hyoza";
    static final String WEB_SOCKET_TOPIC = "/topic/request";

    BlockingQueue<String> blockingQueue;
    WebSocketStompClient stompClient;

    @Autowired
    HelpUserRepository helpUserRepository;
    @Autowired
    HelpRequestService helpRequestService;
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort Integer port =8082;

    @BeforeEach
    public void beforeEach() {

         //발신자와 수신자 회원가입
        helpUserRepository.save(HelpUserEntity.builder()
                .userEmail("test@naver.com")
                //.refreshToken("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyRW1haWwiOiJkbGdvdGhkMzk2NkBuYXZlci5jb20iLCJpYXQiOjE2OTE2NTY5MzcsImV4cCI6MTY5Njg0MDkzN30.XtupI41XAnmCxZJHHiki1BsxXdTIkkp5CDTkAzEywPc")
                .userName("김요청")
                .userPhone("010-1234-0710")
                .userRole("HELP")


                .build());

        helpUserRepository.save(HelpUserEntity.builder()
                .userEmail("test2@naver.com")
                //.refreshToken("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyRW1haWwiOiJkbGdvdGhkMzk2NkBuYXZlci5jb20iLCJpYXQiOjE2OTE2NTY5MzcsImV4cCI6MTY5Njg0MDkzN30.XtupI41XAnmCxZJHHiki1BsxXdTIkkp5CDTkAzEywPc")
                .userName("홍도움")
                .userPhone("010-1111-2222")
                .userRole("HELPER")
                .build());


        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(
                Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));

    }

    @Test
    public void connectionFailedByInvalidateTokenTest() { // 유효하지않은 토큰 연결 테스트

        // given
        StompHeaders headers = new StompHeaders(); // 헤더에 토큰 값 삽입
        headers.add("token", "invalidate token");

        // when, then
        // 잘못된 토큰으로 연결하면 예외 발생
        Assertions.assertThatThrownBy(() -> {
            stompClient
                    .connect(getWsPath(), new WebSocketHttpHeaders() ,headers, new StompSessionHandlerAdapter() {})
                    .get(10, SECONDS);
        }).isInstanceOf(ExecutionException.class);
    }

    @Test
    public void alarmByMessageTest() throws Exception { // 메시지 수신 시 알람 테스트

        // given
        HelpUserEntity sender = helpUserRepository.findByUserEmail("test@naver.com");
        HelpUserEntity receiver = helpUserRepository.findByUserEmail("test2@naver.com");
        StompHeaders headers = new StompHeaders(); // 헤더에 토큰 삽입
       // headers.add("Authorization", sender.getRefreshToken());
        StompSession session = stompClient
                .connect(getWsPath(), new WebSocketHttpHeaders() ,headers, new StompSessionHandlerAdapter() {})
                .get(10, SECONDS); // 연결
        session.subscribe(WEB_SOCKET_TOPIC ,new DefaultStompFrameHandler()); // "/sub/{userId}" 구독
        HelpRequestDTO helpRequestDTO = new HelpRequestDTO();
        helpRequestDTO.setUserEmail(sender.getUserEmail());
        helpRequestDTO.setHelpName("Help Request Test");
        restTemplate.postForObject("/help/helprequest/requestHelp", helpRequestDTO, Void.class);
        String receivedMessage = blockingQueue.poll(5, TimeUnit.SECONDS);

        // Assertions
        System.out.println("Received Message: " + receivedMessage);
        // Perform your assertions here based on the received message
    }
    class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            blockingQueue.offer(new String((byte[]) o));
        }
    }

    private String getWsPath() {
        return WEB_SOCKET_URI;
    }

    /**
     * 테스트 코드 관련 글
     * https://kukekyakya.tistory.com/12
     *
     * **/

}
