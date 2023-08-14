package com.help.hyozason_backend.repository.helpboard;

import com.help.hyozason_backend.entity.helpboard.HelpBoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelpBoardRepository extends JpaRepository<HelpBoardEntity,Long> {
    Page<HelpBoardEntity> findByHelpLocation_Region2DepthName(String region_2depth_name, Pageable pageable);
}
