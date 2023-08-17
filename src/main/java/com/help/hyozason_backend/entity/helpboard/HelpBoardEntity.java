package com.help.hyozason_backend.entity.helpboard;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data //get,set 메소드 이용가능하게 하는 어노테이션
@Table(name = "HelpBoard")
public class HelpBoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//는 JPA에서 기본 키를 자동으로 생성할 때 사용하는 방법 중 하나
    @Column(name="helpId")
    long helpId;

    @Column(name = "helpName")
    String helpName;

    @Column(name = "helpCategory")
    String helpCategory;

    @Column(name = "helpAccept")
    String helpAccept;

    @Column(name="createdAt")
    LocalDateTime createdAt;

    @Column(name = "userEmail")
    String userEmail;

    @Column(name = "locationInfo")
    String locationInfo;


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
