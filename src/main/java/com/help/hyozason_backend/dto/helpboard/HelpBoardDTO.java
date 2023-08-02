package com.help.hyozason_backend.dto.helpboard;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor // 기본 생성자 자동으로 만들어줌
@ToString //toString 메서드 자동으로 만들어줌
public class HelpBoardDTO {
    long helpId;
    long userId;
    String helpName;
    String helpCategory;
    String helpAccept;
}
