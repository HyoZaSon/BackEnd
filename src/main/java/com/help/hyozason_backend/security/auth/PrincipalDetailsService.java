package com.help.hyozason_backend.security.auth;

import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.exception.BaseException;
import com.help.hyozason_backend.exception.MemberErrorCode;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final HelpUserRepository helpUserRepository;

    @Override
    public PrincipalDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HelpUserEntity memberEntity = helpUserRepository.findByIdWithStatus(Long.parseLong(username))
                .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));
        return new PrincipalDetails(memberEntity);
    }
}


