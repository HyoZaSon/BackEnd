package com.help.hyozason_backend.dto.helplocation;

import lombok.*;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor // 기본 생성자 자동으로 만들어줌
@AllArgsConstructor
@Builder
@ToString //toString 메서드 자동으로 만들어줌

public class HelpLocationDTO {
    String locationId;
    String longitude;
    String latitude;
    LocalDateTime createdAt;
    String userEmail;

}
