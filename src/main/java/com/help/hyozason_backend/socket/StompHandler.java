package com.help.hyozason_backend.socket;

import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;
import com.help.hyozason_backend.security.jwt.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class StompHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final HelpUserRepository helpUserRepository;
    /***
     *
     * StompHeaderAccessor.wrap으로 message를 감싸면 STOMP의 헤더에 직접 접근할 수 있습니다.
     * 위에서 작성한 클라이언트에서 보낸 JWT가 들어있는 헤더 Authorization을
     * StompHeaderAccessor.getNativeHeader("Authorization") 메서드를 통해 받아올 수 있고
     * 클라이언트가 connect 할때 받아온 헤더의 값은 JWT가 됩니다. 받은 JWT를 검증해 정상적으로 소켓을 사용할 수 있도록 동작합니다.
     ***/

    @Autowired
    public StompHandler(JwtTokenProvider jwtTokenProvider, HelpUserRepository helpUserRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.helpUserRepository = helpUserRepository;
    }
    /*@Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        return message;
    }*/

    /*
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {

            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            System.out.println("message: " + message.getPayload());
            System.out.println("토큰 : " + accessor.getFirstNativeHeader("Authorization"));
            System.out.println("커맨드 : " + accessor.getCommand());

            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                if (accessor.getNativeHeader("Authorization") != null) {
                    //List<String> authorization = accessor.getNativeHeader("Authorization");
                    String accessToken = accessor.getFirstNativeHeader("Authorization");
                    System.out.println("accessToken : " + accessToken);

                    String useremail = jwtTokenProvider.getMemberIdByToken(accessToken);
                    System.out.println("useremail : " + useremail);

                    HelpUserEntity userEntity = helpUserRepository.findByUserEmail(useremail);
                    if (useremail != null) {
                        System.out.println("유저가 있습니다.");
                        if (jwtTokenProvider.validateToken(accessToken)) {
                            System.out.println("토큰이 유효합니다.");
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userEntity.getUserToken(), userEntity, null);

                            if (SecurityContextHolder.getContext().getAuthentication() == null)
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                            accessor.setUser(authentication);
                        } else {
                            System.out.println("토큰이 유효하지 않습니다.");
                        }
                        if (StompCommand.SEND.equals(accessor.getCommand())) {
                            boolean isSent = channel.send(message);
                            System.out.println("Message sent " + isSent);

                            if (isSent)
                                return message;
                        }

                    } else {
                        System.out.println("유저가 없습니다.");
                    }
                }
                //jwtTokenProvider.validateToken(Objects.requireNonNull(accessor.getFirstNativeHeader("Authorization")).substring(7));

            } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (Objects.nonNull(authentication))
                    System.out.println("Disconnected Auth : " + authentication.getName());

                else
                    System.out.println("Disconnected Sess : " + accessor.getSessionId());

            }

            return message;
        } catch (Exception e) {
            // 예외 처리
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            // 예외를 throw하거나 처리하거나, 다른 예외 처리 로직을 추가해야 합니다.
        }

        return message;
    }

*/
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("message: " + message.getPayload());
        System.out.println("토큰 : " + accessor.getFirstNativeHeader("Authorization"));
        System.out.println("커맨드 : " + accessor.getCommand());

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String accessToken = accessor.getFirstNativeHeader("Authorization");
            String userEmail = jwtTokenProvider.getMemberIdByToken(accessToken);
            HelpUserEntity userEntity = helpUserRepository.findByUserEmail(userEmail);

            if (userEntity != null && jwtTokenProvider.validateToken(accessToken)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userEntity.getUserToken(), userEntity, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                accessor.setUser(authentication);
            }
        }

        return message;
    }

    }
