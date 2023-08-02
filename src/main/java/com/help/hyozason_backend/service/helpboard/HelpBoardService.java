package com.help.hyozason_backend.service.helpboard;

import com.help.hyozason_backend.dto.helpboard.HelpBoardDTO;
import com.help.hyozason_backend.entity.helpboard.HelpBoardEntity;
import com.help.hyozason_backend.etc.ResponseService;
import com.help.hyozason_backend.mapper.helpboard.HelpBoardMapper;
import com.help.hyozason_backend.repository.helpboard.HelpBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HelpBoardService extends ResponseService {
    private final HelpBoardRepository helpBoardRepository;
    @Autowired
    public HelpBoardService(HelpBoardRepository helpBoardRepository) {
        this.helpBoardRepository = helpBoardRepository;
    }

    public ResponseEntity<List<HelpBoardDTO>> getHelpBoards(Pageable pageable) {
        try {
            Page<HelpBoardEntity> results = helpBoardRepository.findAll(pageable);
            List<HelpBoardDTO> helpBoardDTOList = results.getContent().stream()
                    .map(HelpBoardMapper.INSTANCE::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(helpBoardDTOList);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
