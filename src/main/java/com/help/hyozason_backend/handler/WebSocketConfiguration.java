package com.help.hyozason_backend.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker //@EnableWebSocketMessageBroker를 통해 메시지 플로우를 모으기 위해 컴포넌트를 구성합니다.
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler; // jwt 토큰 인증 핸들러

    @Autowired
    public WebSocketConfiguration(StompHandler stompHandler) {
        this.stompHandler = stompHandler;
    }

    /*
        private HelpRequestService helpRequestService;

        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            registry
                    .addHandler(signalingSocketHandler(),"/help-request") //웹소켓 서버의 엔드포인트 -> url:port/help-request, 리액트에서도 이걸로 웹소켓 요청 보내면됨
                    .setAllowedOrigins("*"); //클라이언트의 모든 요청 수용
        }

        @Bean
        public WebSocketHandler signalingSocketHandler() {
            return new HelpRequestHandler(helpRequestService);
        }
        */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        /*
         *
         * 소켓 연결 uri
         * 소켓을 연결할때 통신 이용
         * CONNECT : 연결 중
         * CONNECTED : 연결 성공
         * ERROR : 연결 실패  setAllowedOriginPatterns("*") CORS 설정
         *
         * */
        registry.addEndpoint("/ws-hyoza") //handshake endpoint 처음 웹소켓 handshake 경로
                .setAllowedOriginPatterns("*")
                .withSockJS();//소켓 연결
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        /*
         *
         * Stomp를 사용하기 위한 Message Broker 설정해주는 메서드
         * enableSimpleBroker : 메시지를 받을 때, 경로를 설정해주는 함수이다.
         * 스프링에서 제공해주는 내장 브로커
         * "/queue" -> 1:1
         * "/topic" -> 1:N
         * api에 해당 queue 또는 topic이 있을 경우 브로커가 가로챈다.
         * 클라이언트가 메세지를 보낼 때, 경로 앞에 "/app"이 있으면 Broker에게 보내짐
         *
         * */
        //메시지 보내기 요청
        registry.enableSimpleBroker("/queue","/topic");
        //메시지 구독 요청
        registry.setApplicationDestinationPrefixes("/app");//app로 시작되는 stomp message는 -> @Controller클래스 내부에 @MessageMapping 메소드로 라우팅
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler); // 핸들러 등록
    }
}
