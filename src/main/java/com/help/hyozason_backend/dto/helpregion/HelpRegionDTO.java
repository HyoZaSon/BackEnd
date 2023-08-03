package com.help.hyozason_backend.dto.helpregion;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor // 기본 생성자 자동으로 만들어줌
@ToString //toString 메서드 자동으로 만들어줌

public class HelpRegionDTO {
    private long regionId;
    private long userId;
    private String regionInfo;
}
