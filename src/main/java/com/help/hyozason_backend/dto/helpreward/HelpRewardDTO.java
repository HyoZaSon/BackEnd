package com.help.hyozason_backend.dto.helpreward;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor // 기본 생성자 자동으로 만들어줌
@ToString //toString 메서드 자동으로 만들어줌
@Builder
public class HelpRewardDTO {
    int rewardScore;
    String userId;

    public HelpRewardDTO(int rewardScore, String userId) {
        this.rewardScore = rewardScore;
        this.userId = userId;
    }
}
