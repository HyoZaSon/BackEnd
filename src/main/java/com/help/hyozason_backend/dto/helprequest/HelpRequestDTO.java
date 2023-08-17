package com.help.hyozason_backend.dto.helprequest;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HelpRequestDTO {
    //문자 api 구현할때 수정하기
    private long helpId;
    private String userEmail;
    private String helpName;
    private String helpCategory;
    private String helpAccept;
    private String locationInfo;
    private String region_2depth_name;
    private String region_3depth_name;
    private String mountain_yn;
    private String main_address_no;
    private String sub_address_no;
    private String zip_code;



}
