package com.help.hyozason_backend.security.auth;

import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.exception.BaseException;
import com.help.hyozason_backend.exception.MemberErrorCode;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    //이메일 기반으로 사용자 정보 조회 PrincipalDetails객체 반환
    private final HelpUserRepository helpUserRepository;

    @Override
    public PrincipalDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Optional <HelpUserEntity> helpUserEntity = helpUserRepository.findByUserEmail(email);
        HelpUserEntity memberEntity = helpUserRepository.findByUserEmail(email);

        return new PrincipalDetails(memberEntity);
    }
}


