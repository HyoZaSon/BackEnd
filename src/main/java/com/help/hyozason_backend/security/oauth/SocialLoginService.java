package com.help.hyozason_backend.security.oauth;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.help.hyozason_backend.exception.AuthErrorCode;
import com.help.hyozason_backend.exception.BaseException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

@Service
@Slf4j
public class SocialLoginService {

    @Value("${social.grant-type}")
    private String grantType;
    @Value("${social.kakao.client-id}")
    private String kakaoClientId;
    @Value("${social.kakao.redirect-uri}")
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
        JsonElement element = new JsonParser().parse(result.toString());

        accessToken = element.getAsJsonObject().get("access_token").getAsString();

        br.close();
        bw.close();

        return accessToken;
    }

    public String getKaKaoEmail(String accessToken) throws IOException {
        String requestUrl = "https://kapi.kakao.com/v2/user/me";
        StringBuilder result = getEmail(accessToken, requestUrl);
        return new JsonParser().parse(result.toString()).getAsJsonObject().get("kakao_account")
                .getAsJsonObject().get("email").getAsString();
    }


    public StringBuilder getEmail(String accessToken, String requestUrl) throws IOException {
        URL url = new URL(requestUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod(HttpMethod.GET.name());
        conn.setRequestProperty("Authorization", " Bearer " + accessToken);

        if (conn.getResponseCode() >= 400) {
            throw new BaseException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        br.close();
        return result;
    }
}