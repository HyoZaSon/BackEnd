package com.help.hyozason_backend.dto.helpuser;
import lombok.*;



@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data //get,set 메소드 이용가능하게 하는 어노테이션
public class HelpUserDTO {
    long userId;
    String userEmail;
    String userToken;
    String userName;
    long userAge;
    String userGender;
    String userPhone;
    String userRole;
}
