package com.help.hyozason_backend.repository.helpreward;

import com.help.hyozason_backend.entity.helpreward.HelpRewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelpRewardRepository extends JpaRepository<HelpRewardEntity,Long> {
}
