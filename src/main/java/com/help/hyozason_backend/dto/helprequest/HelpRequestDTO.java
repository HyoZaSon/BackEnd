package com.help.hyozason_backend.dto.helprequest;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HelpRequestDTO {
    //문자 api 구현할때 수정하기
    private long helpId;
    //private String userEmail;
    private String helpName;
    private String helpCategory;
    private String helpAccept;
    private String longitude;
    private String latitude;



}
