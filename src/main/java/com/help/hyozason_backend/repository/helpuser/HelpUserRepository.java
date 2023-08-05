package com.help.hyozason_backend.repository.helpuser;

import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.etc.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HelpUserRepository extends JpaRepository<HelpUserEntity,Long> {

    Optional<HelpUserEntity> findByEmail(String email);
    Optional<HelpUserEntity> findByRefreshToken(String refreshToken);

    Optional<HelpUserEntity> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}

