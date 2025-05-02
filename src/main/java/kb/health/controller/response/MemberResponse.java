package kb.health.controller.response;

import kb.health.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {
    private Long memberId;
    private String userName;
    private double dayScore;
    private double baseScore;
    private String profileImageUrl;

    public static MemberResponse create(Member member) {
        return MemberResponse.builder()
                .memberId(member.getId())
                .userName(member.getUserName())
                .dayScore(member.getTotalScore())
                .baseScore(member.getBaseScore())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
