package com.help.hyozason_backend.controller.helprequestsse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.help.hyozason_backend.dto.helprequest.HelpRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class SseEmitters {

    private static final AtomicLong counter = new AtomicLong();

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
/**이 콜백이 SseEmitter를 관리하는 다른 스레드에서 실행된다는 것입니다.
 * 따라서 thread-safe한 자료구조를 사용하지 않으면 ConcurrnetModificationException이 발생할 수 있습니다.
 * 여기서는 thread-safe한 자료구조인 CopyOnWriteArrayList를 사용
 * */
    SseEmitter add(SseEmitter emitter) {
        this.emitters.add(emitter);
        log.info("new emitter added: {}", emitter);
        log.info("emitter list size: {}", emitters.size());
        log.info("emitter list: {}", emitters);
        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            this.emitters.remove(emitter);  // 만료되면 리스트에서 삭제
        });
        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
        });

        return emitter;
    }

    public void count() {
        long count = counter.incrementAndGet();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("count")
                        .data(count));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }



    public void sendNewHelpRequest(Long helpRequestId, HelpRequestDTO helpRequestInfo) {

        // HelpRequestInfo를 JSON 문자열로 변환하는 로직 필요 (예: ObjectMapper 사용)
        String jsonHelpRequestInfo = convertToJsonString(helpRequestInfo);

        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .id(helpRequestId.toString())
                .data(jsonHelpRequestInfo)
                .name("newHelpRequest");

        sendEventToEmitter(event);
    }

    public void sendHelpAcceptStatus(Long helpRequestId, String helpAcceptStatus) {
        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .id(helpRequestId.toString())
                .data(helpAcceptStatus)
                .name("helpAcceptStatus");

        sendEventToEmitter(event);
    }



    private void sendEventToEmitter(SseEmitter.SseEventBuilder event) {
        emitters.forEach(emitter -> {
            try {
                emitter.send(event);
            } catch (IOException e) {
                log.error("Error sending SSE event: {}", e.getMessage());
            }
        });
    }

    private String convertToJsonString(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to JSON: {}", e.getMessage());
            return "";
        }
    }



}