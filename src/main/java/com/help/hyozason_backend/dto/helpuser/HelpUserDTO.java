package com.help.hyozason_backend.dto.helpuser;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor // 기본 생성자 자동으로 만들어줌
@ToString //toString 메서드 자동으로 만들어줌
public class HelpUserDTO {
    private String email;
    private String password;
    private String nickname;
    private int age;
    private String city;

}
