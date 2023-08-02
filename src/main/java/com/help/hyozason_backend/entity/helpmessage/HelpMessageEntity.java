package com.help.hyozason_backend.entity.helpmessage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class HelpMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//는 JPA에서 기본 키를 자동으로 생성할 때 사용하는 방법 중 하나
    @Column(name="helpId")
    long helpId;
    //문자 api 구현할때 수정하기
    private String type;
    private String sender;
    private String receiver;
    private Object data;

}
