package com.help.hyozason_backend.repository.helpboard;

import com.help.hyozason_backend.entity.helpboard.HelpBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelpBoardRepository extends JpaRepository<HelpBoardEntity,Long> {
}
