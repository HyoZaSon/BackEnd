package com.help.hyozason_backend.handler;

import com.help.hyozason_backend.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    /***
     *
     * StompHeaderAccessor.wrap으로 message를 감싸면 STOMP의 헤더에 직접 접근할 수 있습니다.
     * 위에서 작성한 클라이언트에서 보낸 JWT가 들어있는 헤더 Authorization을
     * StompHeaderAccessor.getNativeHeader("Authorization") 메서드를 통해 받아올 수 있고
     * 클라이언트가 connect 할때 받아온 헤더의 값은 JWT가 됩니다. 받은 JWT를 검증해 정상적으로 소켓을 사용할 수 있도록 동작합니다.
     ***/

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("message: "+message);
        System.out.println("토큰 : "+accessor.getNativeHeader("authorization"));
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            jwtTokenProvider.validateToken(Objects.requireNonNull(accessor.getFirstNativeHeader("Authorization")).substring(7));
        }
        return message;
    }



}
