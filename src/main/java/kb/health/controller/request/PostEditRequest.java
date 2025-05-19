package kb.health.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostEditRequest {

    @NotBlank(message = "내용은 비어 있을 수 없습니다.")
    private String title;

    // 이미지 URL은 선택 사항
    private String content;
}
