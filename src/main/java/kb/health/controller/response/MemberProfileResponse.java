package kb.health.controller.response;

import kb.health.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MemberProfileResponse {
    private Long memberId;
    private String userName;
    private double totalScore;
    private double baseScore;
    private String profileImageUrl;

    private int followingCount;
    private int followerCount;

    private boolean following;

    private NutritionAchievementResponse todayAchievement;
    private List<DailyScoreResponse> last10DaysScores;

    public static MemberProfileResponse create(Member member, NutritionAchievementResponse todayAchievement, List<DailyScoreResponse> last10DaysScores,
                                               int followingCount, int followerCount) {
        return MemberProfileResponse.builder()
                .memberId(member.getId())
                .userName(member.getUserName())
                .totalScore(member.getTotalScore())
                .baseScore(member.getBaseScore())
                .profileImageUrl(member.getProfileImageUrl())
                .followingCount(followingCount)
                .followerCount(followerCount)
                .todayAchievement(todayAchievement)
                .last10DaysScores(last10DaysScores)
                .build();
    }
}
