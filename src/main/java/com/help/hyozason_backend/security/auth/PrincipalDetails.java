package com.help.hyozason_backend.security.auth;

import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;


public class PrincipalDetails implements UserDetails {

    private HelpUserEntity member;

    public PrincipalDetails(HelpUserEntity member) {
        this.member = member;
    }


    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    public HelpUserEntity getMember() {
        return this.member;
    }

    //사용자의 권한 정보 반환. 사용자의 Role을 SimpleGrantedAuthority객체로 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getUserRole()));
        return authorities;
    }
    //사용자의 비밀 번호 반환
    @Override
    public String getPassword() {
        return encodePwd().encode("this is password");
    }

    @Override
    public String getUsername() {

        return member.getUserEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
