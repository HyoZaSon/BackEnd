package com.help.hyozason_backend.entity.helpuser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data //get,set 메소드 이용가능하게 하는 어노테이션
@Table(name = "HelpUser")
public class HelpUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//는 JPA에서 기본 키를 자동으로 생성할 때 사용하는 방법 중 하나
    @Column(name="userEmail")
    String userEmail;

    @Column(name = "userToken")
    String userToken;

    @Column(name = "userName")
    String userName;

    @Column(name = "userAge")
    long userAge;

    @Column(name = "userGender")
    String userGender;

    @Column(name = "userPhone")
    String userPhone;

    @Column(name = "userRole")
    String userRole;


}
