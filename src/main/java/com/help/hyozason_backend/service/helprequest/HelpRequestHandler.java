package com.help.hyozason_backend.service.helprequest;

import com.help.hyozason_backend.dto.helpmessage.HelpMessageDTO;
import jakarta.websocket.MessageHandler;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Slf4j
public class HelpRequestHandler extends TextWebSocketHandler {

    // 로그인 한 전체 session 리스트
    List<WebSocketSession> sessions = new ArrayList<WebSocketSession>();
    // 1대1
    Map<String, WebSocketSession> userSessionsMap = new HashMap<String, WebSocketSession>();
    // 현재 로그인 중인 개별 유저
    Map<String, WebSocketSession> users = new ConcurrentHashMap<String, WebSocketSession>();
//private static final Logger logger = (Logger) LoggerFactory.getLogger(WebSocketHandler.class);

    //웹 소켓 연결
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        //logger.info("Socker 연결");
        log.debug("socket 연결");
        sessions.add(session);

        var sessionId = session.getId();
        users.put(sessionId,session);//세션 저장
        //소켓에 연결된 사용자 모두에게 알림을 전송하는것
        HelpMessageDTO helpMessageDTO = HelpMessageDTO.builder().sender(sessionId).receiver("all").build();
        helpMessageDTO.newConnect();

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
    protected void handleTextMessage(WebSocketSession session, TextMessage message){};

    //소켓 연결 종료
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){};

    //소켓 통신 에러
    @Override
    public void handleTransportError(WebSocketSession session,Throwable throwable){};
}
