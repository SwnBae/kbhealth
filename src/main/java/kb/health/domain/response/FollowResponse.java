package kb.health.domain.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowResponse {
    private Long followId;  // Follow 엔티티의 PK
    private String userName;
    private int score;
    private String profileImageUrl;
}
