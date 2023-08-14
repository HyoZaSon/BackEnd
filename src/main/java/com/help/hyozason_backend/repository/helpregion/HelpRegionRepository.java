package com.help.hyozason_backend.repository.helpregion;

import com.help.hyozason_backend.entity.helplocation.HelpLocationEntity;
import com.help.hyozason_backend.entity.helpregion.HelpRegionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpRegionRepository extends JpaRepository<HelpRegionEntity,String>{
    List<HelpRegionEntity> findByRegion2DepthName(String region_2depth_name);
}


