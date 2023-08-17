package com.help.hyozason_backend.repository.helplocation;

import com.help.hyozason_backend.entity.helpboard.HelpBoardEntity;
import com.help.hyozason_backend.entity.helplocation.HelpLocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelpLocationRepository extends JpaRepository<HelpLocationEntity,String> {
    Page<HelpLocationEntity> findAll(Pageable pageable);
}
