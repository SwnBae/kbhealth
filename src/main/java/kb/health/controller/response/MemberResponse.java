package kb.health.controller.response;

import kb.health.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {
    private Long memberId;
    private String userName;
    private int dayScore;
    private int baseScore;
    private String profileImageUrl;

    public static MemberResponse create(Member member) {
        return MemberResponse.builder()
                .memberId(member.getId())
                .userName(member.getUserName())
                .dayScore(member.getDayScore())
                .baseScore(member.getBaseScore())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
