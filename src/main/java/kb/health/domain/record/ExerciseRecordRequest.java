package kb.health.domain.record;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * 삽입, 수정 시 사용될 DTO
 */
@Getter @Setter
public class ExerciseRecordRequest {
    private Long id;

    @NotEmpty(message = "운동 시간은 필수 입니다.")
    private int durationMinutes;
    @NotEmpty(message = "소모 칼로리는 필수 입니다.")
    private int caloriesBurned;

    @NotEmpty(message = "운동 타입은 필수 입니다.")
    private ExerciseType exerciseType;
}
