package com.help.hyozason_backend.entity.helplocation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "HelpLocation")
public class HelpLocationEntity {
    @Id
    @Column(name="locationInfo")
    String locationInfo;

    @Column(name = "longitude")
    String longitude;

    @Column(name = "latitude")
    String latitude;

    @Column(name="createdAt", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "userEmail")
    String userEmail;
}
