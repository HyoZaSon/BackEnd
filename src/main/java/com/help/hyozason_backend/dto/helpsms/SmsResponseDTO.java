package com.help.hyozason_backend.dto.helpsms;

import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class SmsResponseDTO {
    String requestId;
    LocalDateTime requestTime;
    String statusCode;
    String statusName;
}
