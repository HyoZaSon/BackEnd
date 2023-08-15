package com.help.hyozason_backend.security.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : member") //Principal 매개변수로 선언할 필요 x
public @interface CurrentMember {
}

//해당 매개변수에 현재 인증된 사용자의 정보를 주입하기 위해 사용