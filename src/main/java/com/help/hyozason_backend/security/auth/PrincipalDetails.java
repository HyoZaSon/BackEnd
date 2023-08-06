package com.help.hyozason_backend.security.auth;

import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;


public class PrincipalDetails implements UserDetails {
    private final HelpUserEntity helpUser;

    public PrincipalDetails(HelpUserEntity member) {
        this.helpUser = member;
    }

    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    public HelpUserEntity getMember() {
        return this.helpUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(helpUser.getUserRole()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return encodePwd().encode("this is password");
    }

    @Override
    public String getUsername() {

        return helpUser.getUserEmail();
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
        return !helpUser.isStatus();
    }
}
