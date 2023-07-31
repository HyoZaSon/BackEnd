package com.help.hyozason_backend.service.helprequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket //웹 소켓 서버 사용하도록 정의
public class WebSocketConfiguration implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(signalingSocketHandler(),"/alarm") //웹소켓 서버의 엔드포인트 -> url:port/alarm
                .setAllowedOrigins("*"); //클라이언트의 모든 요청 수용
    }

    @Bean
    public WebSocketHandler signalingSocketHandler() {
        return new HelpRequestHandler();
    }
}
