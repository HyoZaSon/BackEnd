package com.help.hyozason_backend.dto.helpregion;
import jakarta.persistence.Column;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor // 기본 생성자 자동으로 만들어줌
@ToString //toString 메서드 자동으로 만들어줌
@AllArgsConstructor
@Builder
public class HelpRegionDTO {

    private long regionId;
    private String userEmail;

    private String regionInfo1;

    private String regionInfo2;


}
