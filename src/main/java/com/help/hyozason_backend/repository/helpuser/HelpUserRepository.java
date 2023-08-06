package com.help.hyozason_backend.repository.helpuser;

import com.help.hyozason_backend.entity.helpboard.HelpBoardEntity;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HelpUserRepository extends JpaRepository<HelpUserEntity,String> {
    HelpUserEntity findByUserEmail(String userEmail);

public interface HelpUserRepository extends JpaRepository<HelpUserEntity,Long> {


    @Query(value = "select m from Member m where m.id = :memberId and m.status = true")
    Optional<HelpUserEntity> findByIdWithStatus(@Param("memberId") Long memberId);
    Optional<HelpUserEntity> findByEmail(String email);
    Boolean existsByNickName(String nickName);
    Boolean existsByEmail(String email);

    Optional<HelpUserEntity> findByIdAndStatusIsTrue(Long id);

    HelpUserEntity findByNickName(String nickName);
}
