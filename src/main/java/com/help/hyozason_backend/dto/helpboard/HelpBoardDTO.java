package com.help.hyozason_backend.dto.helpboard;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자 자동으로 만들어줌
@ToString //toString 메서드 자동으로 만들어줌
@Builder
@AllArgsConstructor
public class HelpBoardDTO {
    private long helpId;
    private String helpUserEmail;
    private String helpName;
    private String helpCategory;
    private String helpAccept;
}
