package com.help.hyozason_backend.entity.helplocation;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data //get,set 메소드 이용가능하게 하는 어노테이션

@Table(name = "HelpLocation")
public class HelpLocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//는 JPA에서 기본 키를 자동으로 생성할 때 사용하는 방법 중 하나
    @Column(name="locationId")
    long helpId;
    long userId;

    String longitude;
    String latitude;


}
