package kb.health.domain.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 수정 시 사용될 DTO
 */

@Getter @Setter
public class MemberEditRequest {
    private Long id;

    @NotEmpty(message = "패스워드는 필수 입니다.")
    private String password;
    @NotEmpty(message = "닉네임은 필수 입니다.")
    private String userName;

    private String profileImageUrl;
}
