package com.help.hyozason_backend.service.helpoauth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.help.hyozason_backend.dto.helpuser.HelpUserDTO;
import com.help.hyozason_backend.etc.ResponseService;
import com.help.hyozason_backend.exception.AuthErrorCode;
import com.help.hyozason_backend.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
@Service
@Slf4j
@RequiredArgsConstructor
public class HelpOauthService extends ResponseService {

    @Value("${grant-type}")
    private String grantType;
    @Value("${kakao.client-id}")
    private String kakaoClientId;
    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUrl;


    public String getKaKaoAccessToken(String idToken) throws IOException {
        String reqUrl = "https://kauth.kakao.com/oauth/token";
        String parameter = "grant_type=" + grantType +
                "&client_id=" + kakaoClientId + // REST_API_KEY
                "&redirect_uri=" + kakaoRedirectUrl + // REDIRECT_URI
                "&code=" + idToken;
        return getAccessToken(idToken, reqUrl, parameter);
    }

    public String getAccessToken(String idToken, String requestUrl, String parameter) throws IOException {
        String accessToken = "";

        URL url = new URL(requestUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod(HttpMethod.POST.name());
        conn.setDoOutput(true);

        // POST 요청에서 필요한 파라미터를 OutputStream을 통해 전송
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(parameter);
        bw.flush();


        if (conn.getResponseCode() >= 400) {
            throw new BaseException(AuthErrorCode.INVALID_ID_TOKEN);
        }

        // 요청을 통해 얻은 데이터를 InputStreamReader을 통해 읽어 오기
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        StringBuilder result = new StringBuilder();

        while ((line = br.readLine()) != null) {
            result.append(line);
        }

        JsonElement element = JsonParser.parseString(result.toString());

        if(element.isJsonObject()) {
            JsonElement accessTokenElement = element.getAsJsonObject().get("access_token");
//            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            if (accessTokenElement != null && accessTokenElement.isJsonPrimitive()) {
                accessToken = accessTokenElement.getAsString();
            } else {
                throw new BaseException(AuthErrorCode.NOT_FOUND_ACCESS_TOKEN);
            }
        }

        br.close();
        bw.close();

        return accessToken;
    }

    public HelpUserDTO getKaKaoEmail(String accessToken, HelpUserDTO helpUserDTO) throws IOException {
        String requestUrl = "https://kapi.kakao.com/v2/user/me";
        JsonObject result = getData(accessToken, requestUrl);
        JsonObject kakaoAccount = result.getAsJsonObject("kakao_account");
        JsonObject properties = result.getAsJsonObject("properties");
        String nickname = properties.get("nickname").getAsString();
        String ageRange = kakaoAccount.get("age_range").getAsString();
        int userAge = Integer.parseInt(ageRange.substring(0, 2));

        helpUserDTO.setUserEmail(kakaoAccount.get("email").getAsString());
        helpUserDTO.setUserGender(kakaoAccount.get("gender").getAsString());
        helpUserDTO.setUserAge(userAge);
        helpUserDTO.setUserName(nickname);
        return helpUserDTO;

    }

    public JsonObject getData(String accessToken, String requestUrl) throws IOException {
        URL url = new URL(requestUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod(HttpMethod.GET.name());
        conn.setRequestProperty("Authorization", " Bearer " + accessToken);

        if (conn.getResponseCode() >= 400) {
            throw new BaseException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            return JsonParser.parseString(result.toString()).getAsJsonObject();
        }
    }
}
