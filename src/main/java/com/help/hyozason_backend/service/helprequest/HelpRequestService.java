package com.help.hyozason_backend.service.helprequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.help.hyozason_backend.dto.helpboard.HelpBoardDTO;
import com.help.hyozason_backend.dto.helplocation.HelpLocationDTO;
import com.help.hyozason_backend.dto.helprequest.HelpRequestDTO;
import com.help.hyozason_backend.dto.helpsms.MessageDTO;
import com.help.hyozason_backend.entity.helpboard.HelpBoardEntity;
import com.help.hyozason_backend.entity.helplocation.HelpLocationEntity;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.etc.ResponseService;
//import com.help.hyozason_backend.handler.HelpRequestHandler;
import com.help.hyozason_backend.mapper.helpboard.HelpBoardMapper;
import com.help.hyozason_backend.mapper.helplocation.HelpLocationMapper;
import com.help.hyozason_backend.repository.helpboard.HelpBoardRepository;
import com.help.hyozason_backend.repository.helplocation.HelpLocationRepository;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;
import com.help.hyozason_backend.service.helpsms.HelpSmsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class HelpRequestService extends ResponseService {
    private final HelpBoardRepository helpBoardRepository;
    private final HelpLocationRepository helpLocationRepository;
    private final HelpUserRepository helpUserRepository;
    //private HelpRequestHandler helpRequestHandler;
    private final HelpSmsService helpSmsService;
    private final SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public HelpRequestService(HelpBoardRepository helpBoardRepository, HelpLocationRepository helpLocationRepository, HelpUserRepository helpUserRepository, HelpSmsService helpSmsService, SimpMessageSendingOperations messagingTemplate) {
        this.helpBoardRepository = helpBoardRepository;
        this.helpLocationRepository = helpLocationRepository;
        this.helpUserRepository = helpUserRepository;
        //this.helpRequestHandler = helpRequestHandler;
        this.helpSmsService = helpSmsService;
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
                    .helpCategory(helpRequestDTO.getHelpCategory())
                    .helpAccept(helpRequestDTO.getHelpAccept())
                    .build();
            //RequestDTO 를 locationDTO로 매핑
            HelpLocationDTO locationDTO = HelpLocationDTO.builder()
                    .locationInfo(helpRequestDTO.getLocationInfo())
                    .userEmail(userEmail)
                    .region_2depth_name(helpRequestDTO.getRegion_2depth_name())
                    .region_3depth_name(helpRequestDTO.getRegion_3depth_name())
                    .mountain_yn(helpRequestDTO.getMountain_yn())
                    .main_address_no(helpRequestDTO.getMain_address_no())
                    .sub_address_no(helpRequestDTO.getSub_address_no())
                    .zip_code(helpRequestDTO.getZip_code())
                    .build();

            //HelpBoardDTO 를 Entity로 매핑
            HelpBoardEntity helpBoardEntity = HelpBoardMapper.INSTANCE.toEntity(helpBoardDTO);
            //HelpLocationDTO 를 Entity로 매핑
            HelpLocationEntity helpLocationEntity = HelpLocationMapper.INSTANCE.toEntity(locationDTO);

            helpBoardRepository.save(helpBoardEntity);
            helpLocationRepository.save(helpLocationEntity);

            //entity 는 save 메소드 호출 이후에 id 필드에 자동으로 값이 할당된다고 함.
            helpId =helpBoardEntity.getHelpId();

            //userEmail을 이용하여 usertable 에서 유저 phone 정보 가져온다.
            HelpUserEntity helpUserEntity = helpUserRepository.findByUserEmail(userEmail);


            //여기에 문자 api
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setTo(helpUserEntity.getUserPhone());
            messageDTO.setContent("도움 요청중입니다. 잠시만 기다려주십시오");
            helpSmsService.sendSms(messageDTO);


            return helpId;

        }catch (Exception e){
            e.printStackTrace();
        }
        return helpId;
    }

    //사용자 B가 도움을 수락했을때의 비즈니스 로직

    @Transactional
    public String acceptHelpRequest(Long helpBoardId) {

        HelpBoardEntity helpBoardentity = helpBoardRepository.findById(helpBoardId)
                .orElseThrow(() -> new EntityNotFoundException("HelpBoard not found"));

        //Board 가 존재하면 Board의 accept 값을 accept 로 변경
        //accept 값이 accept 가 아닐때에만 - 이미 수락하지 않은 경우
        if(!helpBoardentity.getHelpAccept().equals("Accept")){
            helpBoardentity.setHelpAccept("Accept");
            //Board의 accept 값을 accept로 변경하면서 Board의 정보를 다시 저장
            helpBoardRepository.save(helpBoardentity);
            //Board에서 userEmail 을 return
            return helpBoardentity.getUserEmail();
        }
        else{
            //이미 다른사람이 수락한 경우 helpId 에 해당하는 글 삭제
            helpBoardRepository.deleteById(helpBoardId);
            return null;
        }

    }


    public void notifyUserHelpAccepted(Long helpRequestId, HelpUserEntity helperUserEntity, String helpEmail) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        // 사용자 A 개별 큐로 도움 요청 수락 결과 알림 보내기
        String topicPath = "/topic/request/" + helpRequestId;

        HelpUserEntity helpUserEntity = helpUserRepository.findByUserEmail(helpEmail); //도움 요청자 정보 확인

        //사용자 A (특정 사용자)에게 메시지 보내기 위한 정보, 메시지 보낼 주소, 메시지 내용
        messagingTemplate.convertAndSendToUser(helpEmail, topicPath, "도움 요청이 수락되었습니다.");
        messagingTemplate.convertAndSend(topicPath, "도움 요청이 수락되었습니다.");
        //여기에 문자 api 구현하기


        //도움 요청 자
        MessageDTO messageHelpDTO = new MessageDTO();
        messageHelpDTO.setTo(helpUserEntity.getUserPhone());
        messageHelpDTO.setContent("도움 요청이 수락되었습니다.\n"+
                "도움을 수락한 사용자의 연락처 :"+helperUserEntity.getUserPhone()
                +"\n도움을 수락한 사용자의 이름 :"+helperUserEntity.getUserName()
        );

        helpSmsService.sendSms(messageHelpDTO);


        //도움 수락 자
        MessageDTO messageHelperDTO = new MessageDTO();
        messageHelperDTO.setTo(helperUserEntity.getUserPhone());
        messageHelperDTO.setContent("도움 요청이 수락되었습니다.\n"+
                "도움을 요청한 사용자의 연락처 :"+helpUserEntity.getUserPhone()
                +"\n도움을 요청한 사용자의 이름 :"+helpUserEntity.getUserName()
        );

        helpSmsService.sendSms(messageHelperDTO);


    }
}
