package com.help.hyozason_backend.etc;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ROLE_HELPER("HELPER"),
    ROLE_HELP("HELP"),

//    ROLE_MEMBER("member"),
    ROLE_MANAGER("manager");



    private String value;

    @JsonCreator
    public static Role from(String s) {
        return Role.valueOf(s.toUpperCase());
    }
}
