package com.help.hyozason_backend.controller.helpsms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.help.hyozason_backend.dto.helpsms.MessageDTO;
import com.help.hyozason_backend.dto.helpsms.SmsResponseDTO;
import com.help.hyozason_backend.service.helpsms.HelpSmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/help")
public class HelpSmsController {
    private final HelpSmsService helpSmsService;

    //@PostMapping("/send")
    public String sendSms(@RequestBody MessageDTO messageDto, Model model) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        SmsResponseDTO response = helpSmsService.sendSms(messageDto);
        model.addAttribute("response", response);
        return "result";
    }
}
