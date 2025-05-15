package kb.health.controller.request;

import jakarta.validation.constraints.NotEmpty;
import kb.health.domain.record.ExerciseType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 삽입, 수정 시 사용될 DTO
 */
@Getter @Setter
@NoArgsConstructor
public class ExerciseRecordRequest {
    private Long id;

    @NotEmpty(message = "운동 이름은 필수 입니다.")
    private String exerciseName;

    @NotEmpty(message = "운동 시간은 필수 입니다.")
    private int durationMinutes;

    @NotEmpty(message = "소모 칼로리는 필수 입니다.")
    private int caloriesBurned;

    @NotEmpty(message = "운동 타입은 필수 입니다.")
    private ExerciseType exerciseType;

    private String erImgUrl;

    public ExerciseRecordRequest(String exerciseName, int durationMinutes, int caloriesBurned, ExerciseType exerciseType, String erImgUrl) {
        this.exerciseName = exerciseName;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.exerciseType = exerciseType;
        this.erImgUrl = erImgUrl;
    }
}
