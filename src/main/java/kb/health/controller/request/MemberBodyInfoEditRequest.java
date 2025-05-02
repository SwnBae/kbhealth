package kb.health.controller.request;

import jakarta.validation.constraints.NotNull;
import kb.health.domain.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberBodyInfoEditRequest {

    private Long id;  // 멤버 ID 추가

    @NotNull(message = "키는 필수 입니다.")
    private Double height;

    @NotNull(message = "몸무게는 필수 입니다.")
    private Double weight;

    @NotNull(message = "성별은 필수 입니다.")
    private Gender gender;

    @NotNull(message = "나이는 필수 입니다.")
    private Integer age;
}
