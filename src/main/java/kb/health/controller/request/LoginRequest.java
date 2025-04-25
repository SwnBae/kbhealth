package kb.health.controller.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequest {

    @NotEmpty(message = "아이디를 입력해 주십시오")
    private String account;

    @NotEmpty(message = "비밀번호를 입력해 주십시오")
    private String password;
}
