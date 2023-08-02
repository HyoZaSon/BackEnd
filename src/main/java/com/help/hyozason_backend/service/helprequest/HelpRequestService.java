package com.help.hyozason_backend.service.helprequest;

import com.help.hyozason_backend.dto.helpboard.HelpBoardDTO;
import com.help.hyozason_backend.dto.helplocation.HelpLocationDTO;
import com.help.hyozason_backend.entity.helpboard.HelpBoardEntity;
import com.help.hyozason_backend.handler.HelpRequestHandler;
import com.help.hyozason_backend.repository.helpboard.HelpBoardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HelpRequestService {
    private final HelpBoardRepository helpBoardRepository;
    private HelpRequestHandler helpRequestHandler;
    @Autowired
    public HelpRequestService(HelpBoardRepository helpBoardRepository) {
        this.helpBoardRepository = helpBoardRepository;
    }


    /**
     * 도움 요청
     * 요청 값 : 유저 정보(userEmail기반),helpBoardDTO,유저 위치 (locationDTO)
     * 프론트 웹소켓 통신에서 message 를 보낼때 accept: True or False형식으로 보내야한다.
     * 그러면 그냥 db에 저장할때에는 if문으로 판단해서 추가하는걸로.
     * */
    @Transactional
    public void publishHelpRequest(HelpBoardDTO helpBoardDTO, String user_email, HelpLocationDTO helpLocationDTO){
        //도움 요청 글 작성 및 저장

    }


    //사용자 B가 도움을 수락했을때의 비즈니스 로직
    //이거 api 경로가 다르지? ㅇㅋ
    @Transactional
    public void acceptHelpRequest(Long helpBoardId) {
        // 도움 요청 글의 상태를 업데이트하고 필요한 처리 수행
        // ...

        // 사용자 B의 정보를 도움 요청 글에 추가 저장
        // ...

        // 웹 소켓 핸들러를 통해 사용자 A에게 수락 여부 및 사용자 B의 정보를 알림 전송
        //helpRequestHandler.acceptHelpRequest(helpBoardId, userId);


        HelpBoardEntity helpBoardentity = helpBoardRepository.findById(helpBoardId)
                .orElseThrow(() -> new EntityNotFoundException("HelpBoard not found"));

        // 도움 요청을 수락하면 해당 도움 요청의 helpAccept 값을 true로 업데이트
        //helpBoardentity.setHelpAccept(true);
        //helpBoardRepository.save(helpBoardentity);


    }






}
