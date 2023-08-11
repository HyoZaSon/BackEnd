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
    private String locationInfo;
    private LocalDateTime createdAt;
    private String userEmail;
    private String region_2depth_name;
    private String region_3depth_name;
    private String mountain_yn;
    private String main_address_no;
    private String sub_address_no;
    private String zip_code;


}
