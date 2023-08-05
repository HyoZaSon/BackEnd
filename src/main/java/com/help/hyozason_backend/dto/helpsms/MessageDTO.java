package com.help.hyozason_backend.dto.helpsms;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MessageDTO {
    // 발신자 및 내용
    String to;
    String content;
}
