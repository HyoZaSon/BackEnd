package com.help.hyozason_backend.socket;

//이 클래스는 소켓 연결이 시작되면 자동으로 콜 됩니다.
//서비스(비즈니스 로직) 해당 클래스 (소켓 통신 전용)
// stomp 적용으로 해당 클래스는 컨트롤러로 바꿔야함


import com.help.hyozason_backend.service.helprequest.HelpRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class HelpRequestHandler extends TextWebSocketHandler {

    // 로그인 한 전체 session 리스트
    List<WebSocketSession> sessions = new ArrayList<WebSocketSession>();
    // 1대1
    Map<String, WebSocketSession> userSessionsMap = new HashMap<String, WebSocketSession>();
    // 현재 로그인 중인 개별 유저
    Map<String, WebSocketSession> users = new ConcurrentHashMap<String, WebSocketSession>();
    //private static final Logger logger = (Logger) LoggerFactory.getLogger(WebSocketHandler.class);


    private final HelpRequestService helpRequestService;
    @Autowired
    public HelpRequestHandler(HelpRequestService helpRequestService) {
        this.helpRequestService = helpRequestService;
    }

    /**
     * 도움 요청 글이 작성 되면(도움 요청이 생기면)-> 해당 정보를 웹 소켓 핸들러 내의 메소드를 통해 클라이언트들에게 알린다.
     * 사용자 B가 도움 요청 글을 확인하고 수락하면, 해당 정보를 웹 소켓을 통해 핸들러로 전송한다.
     * 웹 소켓 핸들러에서 사용자 B의 수락 여부 및 정보를 처리하고,  이를 HelpRequestService를 이용하여 도움 요청 글의 상태를 업데이트하고 필요한 처리를 합니다.
     * 사용자 A에게 수락 여부 및 사용자 B의 정보를 웹 소켓을 통해 알립니다.
     *
    **/

    //웹 소켓 연결
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        //logger.info("Socker 연결");
        log.debug("socket 연결");
        sessions.add(session);

        var sessionId = session.getId();
        users.put(sessionId,session);//세션 저장
        userSessionsMap.put(sessionId,session);

        //소켓에 연결된 사용자 모두에게 알림을 전송하는것
        users.values().forEach(s -> {
            //모든 세션에 알림
            try{
                if(!s.getId().equals(sessionId)){
                    s.sendMessage(new TextMessage("새로운 사용자가 입장했습니다"));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        });

    };

    //양방향 데이터 통신
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)throws Exception{
        String payload = message.getPayload();
        log.debug("Received message: {}", payload);

        /*** 클라이언트로부터의 메시지를 처리하는 로직을 추가할 수 있습니다.
         예를 들어, 사용자 A가 도움 요청을 수락하면 해당 정보를 받아서 처리하는 로직을 추가할 수 있습니다.
         이후, 해당 정보를 HelpRequestService를 통해 처리하도록 호출할 수도 있습니다.
         메시지가 {"helpId":123,"userEmail":"도움 요청 제목","category":"도움 카테고리","user":{"id":456,"name":"사용자 이름","phoneNumber":"사용자 전화번호","location":"사용자 위치"}}

            // JSON 형식의 데이터를 파싱하여 HelpBoardDTO 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            HelpBoardDTO helpBoardDTO = objectMapper.readValue(payload, HelpBoardDTO.class);

            // HelpRequestService를 통해 도움 요청 수락 작업 수행
            helpRequestService.acceptHelpRequest(helpBoardDTO.getHelpId());
            // 해당 도움 요청의 글 작성자의 WebSocket 세션을 찾아 알림을 전송


            WebSocketSession targetSession = userSessionsMap.get(helpBoardDTO.getUserId());
            if (targetSession != null) {
                String msg = "도움 요청이 수락되었습니다!";
                sendNotificationToUser(msg, targetSession);
            }
         ***/



    };



    //여기부턴 gpt라서 수정해야함

    // 사용자 A가 도움 요청을 게시하면 사용자 B에게 알림을 보내는 메소드
    public void publishHelpRequest(Long helpBoardId) {
        String message = "새로운 도움 요청이 있습니다!";
        sendNotificationToUser(message, helpBoardId);
    }

    // 사용자 A가 도움 요청을 수락하면 사용자 A에게 알림을 보내는 메소드
    public void acceptHelpRequest(Long helpBoardId) {
        String message = "도움 요청이 수락되었습니다!";
        sendNotificationToUser(message, helpBoardId);




    }

    // 사용자 A가 도움 요청을 거절하면 사용자 A에게 알림을 보내는 메소드
    public void rejectHelpRequest(Long helpBoardId) {
        String message = "도움 요청이 거절되었습니다.";
        sendNotificationToUser(message, helpBoardId);
    }

    private void sendNotificationToUser(String message, Long helpBoardId) {
        // helpBoardId에 해당하는 도움 요청을 수락하거나 거절한 사용자의 WebSocket 세션을 찾아서 알림을 보낼 수 있습니다.
        // 예를 들어, helpBoardId를 통해 도움 요청을 게시한 사용자의 세션을 찾고 알림을 보낼 수 있습니다.
        // WebSocketSession targetSession = userSessionsMap.get(userId); // userId에 해당하는 세션 찾기

        // 간단한 예시로 모든 세션에 알림을 보내는 코드를 작성하겠습니다.
        for (WebSocketSession session : userSessionsMap.values()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("Failed to send message to user", e);
            }
        }
    }



    //소켓 연결 종료
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)throws Exception{
        log.debug("socket 연결 종료");

        var sessionId = session.getId();
        sessions.remove(session);
        users.remove(sessionId);
        userSessionsMap.remove(sessionId);
    };

    //소켓 통신 에러
    @Override
    public void handleTransportError(WebSocketSession session,Throwable throwable){};
}

