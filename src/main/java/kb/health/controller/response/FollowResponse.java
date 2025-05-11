package kb.health.controller.response;

import kb.health.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowResponse {
    private Long followId;  // Follow 엔티티의 PK
    private String userName;
    private double baseScore;
    private double totalScore;

    private String profileImageUrl;

    public static FollowResponse create(Member member) {
        return FollowResponse.builder()
                .followId(member.getId())
                .userName(member.getUserName())
                .totalScore(member.getTotalScore())
                .baseScore(member.getBaseScore())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
