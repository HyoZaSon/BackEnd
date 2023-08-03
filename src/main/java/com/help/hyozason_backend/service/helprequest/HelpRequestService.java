package com.help.hyozason_backend.service.helprequest;

import com.help.hyozason_backend.dto.helpboard.HelpBoardDTO;
import com.help.hyozason_backend.dto.helprequest.HelpRequestDTO;
import com.help.hyozason_backend.entity.helpboard.HelpBoardEntity;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.etc.HelpResponse;
import com.help.hyozason_backend.handler.HelpRequestHandler;
import com.help.hyozason_backend.mapper.helpboard.HelpBoardMapper;
import com.help.hyozason_backend.repository.helpboard.HelpBoardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HelpRequestService extends HelpResponse {
    private final HelpBoardRepository helpBoardRepository;
    private HelpRequestHandler helpRequestHandler;
    private final SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public HelpRequestService(HelpBoardRepository helpBoardRepository, SimpMessageSendingOperations messagingTemplate) {
        this.helpBoardRepository = helpBoardRepository;
        this.messagingTemplate = messagingTemplate;
    }


    /**
     * 도움 요청
     * 요청 값 : 유저 정보(userEmail기반),helpBoardDTO,유저 위치 (locationDTO)
     * 프론트 웹소켓 통신에서 message 를 보낼때 accept: True or False형식으로 보내야한다.
     * 그러면 그냥 db에 저장할때에는 if문으로 판단해서 추가하는걸로.
     * */

    @Transactional
    public long writeHelpRequest(HelpRequestDTO helpRequestDTO, String userEmail) {
        Long helpId =null;
        //도움 요청 글 작성 및 저장
        try{
            //RequestDTO 를 BoardDTO로 매핑
            HelpBoardDTO helpBoardDTO = HelpBoardDTO.builder()
                    .helpName(helpRequestDTO.getHelpName())
                    .helpUserEmail(userEmail)
                    .helpCategory(helpRequestDTO.getHelpCategory())
                    .helpAccept(helpRequestDTO.getHelpAccept())
                    .build();
            //HelpBoardDTO 를 Entity로 매핑
            HelpBoardEntity helpBoardEntity = HelpBoardMapper.INSTANCE.toEntity(helpBoardDTO);
            helpBoardRepository.save(helpBoardEntity);
            //entity 는 save 메소드 호출 이후에 id 필드에 자동으로 값이 할당된다고 함.
            helpId =helpBoardEntity.getHelpId();
            return helpId;

        }catch (Exception e){
            e.printStackTrace();
        }
        return helpId;
    }

    //사용자 B가 도움을 수락했을때의 비즈니스 로직

    @Transactional
    public void acceptHelpRequest(Long helpBoardId) {
        // 도움 요청 글의 상태를 업데이트하고 필요한 처리 수행
        //accept 값 변경 코드 필요


        HelpBoardEntity helpBoardentity = helpBoardRepository.findById(helpBoardId)
                .orElseThrow(() -> new EntityNotFoundException("HelpBoard not found"));


        // 도움 요청을 수락하면 해당 도움 요청의 helpAccept 값을 true로 업데이트
        //helpBoardentity.setHelpAccept(true);
        //helpBoardRepository.save(helpBoardentity);


    }


    public void notifyUserHelpAccepted(Long helpRequestId, HelpUserEntity helpUserEntity) {
        // 사용자 A 개별 큐로 도움 요청 수락 결과 알림 보내기
        String topicPath = "/topic/request/" + helpRequestId;
        //사용자 A의 정보가 있어야 함 전화번호라던가..
        //지금 쓴건 사용자 B의 정보

        //사용자 A (특정 사용자)에게 메시지 보내기 위한 정보, 메시지 보낼 주소, 메시지 내용
        messagingTemplate.convertAndSendToUser(helpUserEntity.getUserEmail(), topicPath, "도움 요청이 수락되었습니다.");
        //여기에 문자 api 구현하기


    }
}
