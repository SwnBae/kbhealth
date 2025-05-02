package kb.health.controller.response;

import kb.health.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class MemberResponse {
    private Long memberId;
    private String userName;
    private double totalScore;
    private double baseScore;
    private String profileImageUrl;

    private NutritionAchievementResponse todayAchievement;
    private List<DailyScoreResponse> last10DaysScores = new ArrayList<>();

    public static MemberResponse create(Member member, NutritionAchievementResponse todayAchievement, List<DailyScoreResponse> last10DaysScores) {
        return MemberResponse.builder()
                .memberId(member.getId())
                .userName(member.getUserName())
                .totalScore(member.getTotalScore())
                .baseScore(member.getBaseScore())
                .profileImageUrl(member.getProfileImageUrl())
                .todayAchievement(todayAchievement)
                .last10DaysScores(last10DaysScores)
                .build();
    }
}
