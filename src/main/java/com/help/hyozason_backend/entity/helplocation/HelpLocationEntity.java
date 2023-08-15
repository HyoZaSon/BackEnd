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

    @Column(name="createdAt", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "userEmail")
    String userEmail;
    @Column(name = "region_2depth_name")
    private String region_2depth_name;
    @Column(name = "region_3depth_name")
    private String region_3depth_name;
    @Column(name = "mountain_yn")
    private String mountain_yn;
    @Column(name = "main_address_no")
    private String main_address_no;
    @Column(name = "sub_address_no")
    private String sub_address_no;
    @Column(name = "zip_code")
    private String zip_code;
}
