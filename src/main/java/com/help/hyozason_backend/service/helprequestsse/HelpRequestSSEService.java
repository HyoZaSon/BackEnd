package com.help.hyozason_backend.service.helprequestsse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.help.hyozason_backend.controller.helprequestsse.SseEmitters;
import com.help.hyozason_backend.dto.helpboard.HelpBoardDTO;
import com.help.hyozason_backend.dto.helplocation.HelpLocationDTO;
import com.help.hyozason_backend.dto.helprequest.HelpRequestDTO;
import com.help.hyozason_backend.dto.helpsms.MessageDTO;
import com.help.hyozason_backend.entity.helpboard.HelpBoardEntity;
import com.help.hyozason_backend.entity.helplocation.HelpLocationEntity;
import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.etc.HelpResponse;
import com.help.hyozason_backend.mapper.helpboard.HelpBoardMapper;
import com.help.hyozason_backend.mapper.helplocation.HelpLocationMapper;
import com.help.hyozason_backend.repository.helpboard.HelpBoardRepository;
import com.help.hyozason_backend.repository.helplocation.HelpLocationRepository;
import com.help.hyozason_backend.repository.helpuser.HelpUserRepository;
import com.help.hyozason_backend.service.helpsms.HelpSmsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.help.hyozason_backend.service.helprequest.HelpRequestService.removeHyphens;

@Service
public class HelpRequestSSEService {
    private final HelpBoardRepository helpBoardRepository;
    private final HelpLocationRepository helpLocationRepository;
    private final HelpUserRepository helpUserRepository;
    private final HelpSmsService helpSmsService;
    private final SseEmitters sseEmitters;
    public HelpRequestSSEService(HelpBoardRepository helpBoardRepository, HelpLocationRepository helpLocationRepository, HelpUserRepository helpUserRepository, HelpSmsService helpSmsService, SseEmitters sseEmitters) {
        this.helpBoardRepository = helpBoardRepository;
        this.helpLocationRepository = helpLocationRepository;
        this.helpUserRepository = helpUserRepository;
        this.helpSmsService = helpSmsService;
        this.sseEmitters = sseEmitters;
    }



    /**
     * 도움 요청 글 작성, 작성 알림
     * **/
    @Transactional
    public long writeHelpRequest(HelpRequestDTO helpRequestDTO, String userEmail) {
        Long helpId =null;
        //도움 요청 글 작성 및 저장
        try{

            // Create HelpLocationEntity
            HelpLocationEntity helpLocation = HelpLocationEntity.builder()
                    .locationInfo(helpRequestDTO.getLocationInfo())
                    .userEmail(userEmail)
                    .region_2depth_name(helpRequestDTO.getRegion_2depth_name())
                    .region_3depth_name(helpRequestDTO.getRegion_3depth_name())
                    .mountain_yn(helpRequestDTO.getMountain_yn())
                    .main_address_no(helpRequestDTO.getMain_address_no())
                    .sub_address_no(helpRequestDTO.getSub_address_no())
                    .zip_code(helpRequestDTO.getZip_code())
                    .build();
            HelpLocationEntity savedHelpLocation = helpLocationRepository.save(helpLocation);

            // Create HelpBoardEntity
            HelpBoardEntity helpBoard = HelpBoardEntity.builder()
                    .helpName(helpRequestDTO.getHelpName())
                    .helpCategory(helpRequestDTO.getHelpCategory())
                    .helpAccept(helpRequestDTO.getHelpAccept())
                    .locationInfo(savedHelpLocation.getLocationInfo())  // Set the saved locationInfo
                    .userEmail(userEmail)
                    .build();


            HelpBoardEntity savedHelpBoard = helpBoardRepository.save(helpBoard);

            //entity 는 save 메소드 호출 이후에 id 필드에 자동으로 값이 할당된다고 함.
            helpId =savedHelpBoard.getHelpId();

            //userEmail을 이용하여 usertable 에서 유저 phone 정보 가져온다.
            HelpUserEntity helpUserEntity = helpUserRepository.findByUserEmail(userEmail);


            //클라이언트에게 전송
            sseEmitters.sendNewHelpRequest(helpId, helpRequestDTO);
            String helpUserPhone = removeHyphens(helpUserEntity.getUserPhone());

            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setTo(helpUserPhone);
            messageDTO.setContent("도움 요청중입니다. 잠시만 기다려주십시오");
            helpSmsService.sendSms(messageDTO);

            return helpId;

        }catch (Exception e){
            e.printStackTrace();
        }
        return helpId;
    }

    /**사용자 B가 도움을 수락했을때의 비즈니스 로직**/
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

        HelpUserEntity helpUserEntity = helpUserRepository.findByUserEmail(helpEmail); //도움 요청자 정보 확인

        //도움 요청 자
        MessageDTO messageHelpDTO = new MessageDTO();
        messageHelpDTO.setTo(removeHyphens(helpUserEntity.getUserPhone()));
        messageHelpDTO.setContent("도움 요청이 수락되었습니다.\n"+
                "도움을 수락한 사용자의 연락처 :"+helperUserEntity.getUserPhone());


        helpSmsService.sendSms(messageHelpDTO);
        // 도움 요청 수락 여부 변경을 클라이언트로 전송
        sseEmitters.sendHelpAcceptStatus(helpRequestId, "accepted");
        // 도움 요청 수락 알림을 클라이언트로 전송
        sseEmitters.sendHelpAcceptStatus(helpRequestId, "help accepted by " + helpEmail);

        //도움 수락 자
        MessageDTO messageHelperDTO = new MessageDTO();
        messageHelperDTO.setTo(removeHyphens((helperUserEntity.getUserPhone())));
        messageHelperDTO.setContent("도움 요청이 수락되었습니다.\n"+
                "도움을 요청한 사용자의 연락처 :"+helpUserEntity.getUserPhone()
        );

        helpSmsService.sendSms(messageHelperDTO);
    }

    public void notifyUserHelpDenided(Long helpBoardId, HelpUserEntity helperUserEntity) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {

        //도움 수락 자
        MessageDTO messageHelperDTO = new MessageDTO();
        messageHelperDTO.setTo(removeHyphens((helperUserEntity.getUserPhone())));
        messageHelperDTO.setContent("도움 요청이 실패하였습니다.\n이미 다른 사용자에게 도움 수락된 글입니다.");
        helpSmsService.sendSms(messageHelperDTO);

        sseEmitters.sendHelpAcceptStatus(helpBoardId, "denied");
        // 도움 요청 수락 알림을 클라이언트로 전송
        sseEmitters.sendHelpAcceptStatus(helpBoardId, "help denied by " +helperUserEntity.getUserEmail());
    }




}
