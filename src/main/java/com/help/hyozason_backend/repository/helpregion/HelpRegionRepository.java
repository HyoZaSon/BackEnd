package com.help.hyozason_backend.repository.helpregion;

import com.help.hyozason_backend.entity.helplocation.HelpLocationEntity;
import com.help.hyozason_backend.entity.helpregion.HelpRegionEntity;
import com.help.hyozason_backend.entity.helpreward.HelpRewardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelpRegionRepository extends JpaRepository<HelpRegionEntity,String>{

    HelpRegionEntity findByUserEmail(String userEmail);

}


