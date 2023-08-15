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
public class HelpUserEntity  {


    @Id
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



    public void changeRefreshToken(String userToken) {
        this.userToken = userToken;
    }

    public HelpUserEntity(String userEmail,String userName,  long userAge, String userGender,String userPhone,String userRole,String userToken ) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userAge = userAge;
        this.userGender = userGender;
        this.userPhone = userPhone;
        this.userRole = userRole;
        this.userToken = userToken;
    }

}
