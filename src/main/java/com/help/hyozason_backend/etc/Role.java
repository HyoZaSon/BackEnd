package com.help.hyozason_backend.etc;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    HELPER("HELPER"),
    HELP("HELP");
    private String value;

    @JsonCreator
    public static Role from(String s) {
        return Role.valueOf(s.toUpperCase());
    }
}
