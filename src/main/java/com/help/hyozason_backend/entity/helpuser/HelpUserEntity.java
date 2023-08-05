package com.help.hyozason_backend.entity.helpuser;
import com.help.hyozason_backend.etc.Role;
import com.help.hyozason_backend.etc.SocialType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import javax.persistence.*;
import static com.help.hyozason_backend.etc.Role.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data //get,set 메소드 이용가능하게 하는 어노테이션
@Table(name = "HelpUser")
public class HelpUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//는 JPA에서 기본 키를 자동으로 생성할 때 사용하는 방법 중 하나
//    @Column(name="userId")
    long userId;

    private String email;

    @Column(unique = true)
    private String socialId;

    private String password;
    private String nickName;
    private String imageUrl;
    private int age;
    private String City;


    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType ;

    private String refreshToken; // 리프레시 토큰

    // 유저 권한 설정 메소드
    public void authorizeHelpUser() {
        this.role = HELP;
    }

    public void authorizeHelperUser() {
        this.role = HELPER;
    }

    public void authorizeUser() {
        this.role = Role.USER;
    }
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }



}
