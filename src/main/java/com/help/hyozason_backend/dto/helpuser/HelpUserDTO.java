package com.help.hyozason_backend.dto.helpuser;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data //get,set 메소드 이용가능하게 하는 어노테이션
public class HelpUserDTO {
    private String userEmail;
    private String userToken;
    private String userName;
    private long userAge;
    private String userGender;
    private String userPhone;
    private String userRole;
}
