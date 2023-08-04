package com.help.hyozason_backend.dto.helplocation;

import java.time.LocalDateTime;

public class HelpLocationDTO {
    String longitude;
    String latitude;
    LocalDateTime createdAt;
    String userEmail;

    public HelpLocationDTO(String longitude, String latitude, LocalDateTime createdAt, String userEmail) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.createdAt = createdAt;
        this.userEmail = userEmail;
    }
}
