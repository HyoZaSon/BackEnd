package com.help.hyozason_backend.entity.helpregion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data //get,set 메소드 이용가능하게 하는 어노테이션
@Table(name = "HelpRegion")
public class HelpRegionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//는 JPA에서 기본 키를 자동으로 생성할 때 사용하는 방법 중 하나
    @Column(name="regionId")
    long regionId;

//    @Column(name = "regionInfo")
//    String regionInfo;

    @Column(name = "region_2depth_name")
    String regionInfo1;

    @Column(name = "region_3depth_name")
    String regionInfo2;

    @Column(name = "userEmail")
    String userEmail;
}
