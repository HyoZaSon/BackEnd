package com.help.hyozason_backend.repository.helpuser;

import com.help.hyozason_backend.entity.helpboard.HelpBoardEntity;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelpUserRepository extends JpaRepository<HelpUserEntity,Long> {
    HelpUserEntity findByEmail(String userEmail);

}
