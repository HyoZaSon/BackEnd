package com.help.hyozason_backend.login.service;

import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final HelpUserRepository helpUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        HelpUserEntity user = helpUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
