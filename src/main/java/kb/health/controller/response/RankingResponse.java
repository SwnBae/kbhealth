package kb.health.controller.response;

import kb.health.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class RankingResponse {
    private int rank; // 순위
    private Long memberId; // 회원 ID
    private String account;
    private String userName; // 사용자 이름
    private String profileImageUrl;
    private double totalScore; // 총 점수
    private double baseScore; // 기본 점수
    private int trend;

    // Member 객체를 사용하여 RankingResponse 객체를 생성하는 팩토리 메서드
    public static RankingResponse create(int rank, Member member) {
        return new RankingResponse(
                rank,
                member.getId(),
                member.getAccount(),
                member.getUserName(),
                member.getProfileImageUrl(),
                member.getTotalScore(),
                member.getBaseScore(),
                0
        );
    }

    // 트렌드 정보를 포함한 생성 메서드
    public static RankingResponse create(int rank, Member member, int trend) {
        return new RankingResponse(
                rank,
                member.getId(),
                member.getAccount(),
                member.getUserName(),
                member.getProfileImageUrl(),
                member.getTotalScore(),
                member.getBaseScore(),
                trend
        );
    }
}
