package com.help.hyozason_backend.repository.helpuser;


import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface HelpUserRepository extends JpaRepository<HelpUserEntity,String> {
    HelpUserEntity findByUserEmail(String userEmail);




}



