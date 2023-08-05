package com.help.hyozason_backend.oauth2;


import com.help.hyozason_backend.entity.helpuser.HelpUserEntity;
import com.help.hyozason_backend.etc.SocialType;
import com.help.hyozason_backend.oauth2.userinfo.KakaoOAuth2UserInfo;
import com.help.hyozason_backend.oauth2.userinfo.OAuth2UserInfo;
import lombok.Builder;
import lombok.Getter;
import com.help.hyozason_backend.etc.Role;
import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {
    private String nameAttributeKey; //로그인 진행 시 키가 되는 값
    private OAuth2UserInfo oAuth2UserInfo;

    @Builder
    public  OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo){
        this.nameAttributeKey= nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    public static OAuthAttributes of(SocialType socialType,String userNameAttributeName, Map<String,Object> attributes){
            return ofKakao(userNameAttributeName, attributes);

    }

    public static OAuthAttributes ofKakao(String userNameAttributeName,Map<String,Object>attributes){
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public HelpUserEntity toEntity(SocialType socialType, OAuth2UserInfo oAuth2UserInfo){
        return HelpUserEntity.builder()
                .socialType(socialType)
                .socialId(oAuth2UserInfo.getId())
                .email(UUID.randomUUID() + "@socialUser.com")
                .nickName(oAuth2UserInfo.getNickname())
                .imageUrl(oAuth2UserInfo.getImageUrl())
                .role(Role.Guest)
                .build();
    }


}
