package kb.health.domain.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberRegistRequest {

    @NotEmpty(message = "계정은 필수 입니다.")
    private String account;

    private String phoneNumber;

    @NotEmpty(message = "패스워드는 필수 입니다.")
    private String password;

    @NotEmpty(message = "닉네임은 필수 입니다.")
    private String userName;
}
