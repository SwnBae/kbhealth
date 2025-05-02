package kb.health.controller.response;

import kb.health.domain.DailyScore;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DailyScoreResponse {
    private final LocalDate date;
    private final double totalScore;

    public static DailyScoreResponse create(DailyScore dailyScore) {
        return new DailyScoreResponse(dailyScore.getDate(), dailyScore.getTotalScore());
    }

}
