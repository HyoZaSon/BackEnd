package com.help.hyozason_backend.dto.helpmessage;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HelpMessageDTO {
    //문자 api 구현할때 수정하기
    private String type;
    private String sender;
    private String receiver;
    private Object data;


    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public void newConnect(){
        this.type = "new";
    }
    public void closeConnect(){
        this.type="close";
    }

}
