package com.help.hyozason_backend.service.helpboard;

import com.help.hyozason_backend.dto.helpboard.HelpBoardDTO;
import com.help.hyozason_backend.entity.helpboard.HelpBoardEntity;
import com.help.hyozason_backend.entity.helpregion.HelpRegionEntity;
import com.help.hyozason_backend.etc.ResponseService;
import com.help.hyozason_backend.mapper.helpboard.HelpBoardMapper;
import com.help.hyozason_backend.repository.helpboard.HelpBoardRepository;
import com.help.hyozason_backend.repository.helpregion.HelpRegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HelpBoardService extends ResponseService {
    private final HelpBoardRepository helpBoardRepository;
    private final HelpRegionRepository helpRegionRepository;

    @Autowired
    public HelpBoardService(HelpBoardRepository helpBoardRepository, HelpRegionRepository helpRegionRepository) {
        this.helpBoardRepository = helpBoardRepository;
        this.helpRegionRepository = helpRegionRepository;
    }

    public ResponseEntity<List<HelpBoardDTO>> getHelpBoards(Pageable pageable, String region_2depth_name) {
        try {
            List<HelpBoardDTO> helpBoardDTOList = new ArrayList<>();

            // region_2depth_name에 맞는 HelpRegionEntity 조회
            List<HelpRegionEntity> matchingRegions = helpRegionRepository.findByRegionInfo1(region_2depth_name);

            // 각 리전 정보별로 HelpBoardEntity 조회하여 DTO로 변환하고 리스트에 추가
            for (HelpRegionEntity region : matchingRegions) {
                Page<HelpBoardEntity> results = helpBoardRepository.findByLocationInfo(region.getRegionInfo1(), pageable);
                helpBoardDTOList.addAll(
                        results.getContent().stream()
                                .map(HelpBoardMapper.INSTANCE::toDTO)
                                .collect(Collectors.toList())
                );
            }

            // 변환된 DTO 리스트를 ResponseEntity에 담아서 반환
            return ResponseEntity.ok(helpBoardDTOList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
