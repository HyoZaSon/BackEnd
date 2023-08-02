package com.help.hyozason_backend.handler;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker //@EnableWebSocketMessageBroker를 통해 메시지 플로우를 모으기 위해 컴포넌트를 구성합니다.
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler; // jwt 토큰 인증 핸들러

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
        registry.addEndpoint("/ws-hyoza") //handshake endpoint 처음 웹소켓 handshake 경로
                .setAllowedOriginPatterns("*")
                .withSockJS();//소켓 연결
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.enableSimpleBroker("/queue","/topic");
        registry.setApplicationDestinationPrefixes("/app");//app로 시작되는 stomp message는 -> @Controller클래스 내부에 @MessageMapping 메소드로 라우팅
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler); // 핸들러 등록
    }
}
