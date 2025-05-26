package kb.health.controller.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kb.health.domain.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberRegistRequest {

    @NotEmpty(message = "계정은 필수 입니다.")
    private String account;

    @NotEmpty(message = "패스워드는 필수 입니다.")
    private String password;

    @NotEmpty(message = "닉네임은 필수 입니다.")
    private String userName;

    @NotNull(message = "키는 필수 입니다.")
    private Double height;

    @NotNull(message = "몸무게는 필수 입니다.")
    private Double weight;

    @NotNull(message = "성별은 필수 입니다.")
    private Gender gender;

    @NotNull(message = "나이는 필수 입니다.")
    private Integer age;
}
