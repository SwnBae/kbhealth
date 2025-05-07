package kb.health.controller.response;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kb.health.domain.record.ExerciseRecord;
import kb.health.domain.record.ExerciseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExerciseRecordResponse {
    private Long id;
    private String exerciseName;
    private int durationMinutes;
    private int caloriesBurned;
    private ExerciseType exerciseType;

    public static ExerciseRecordResponse create(ExerciseRecord record) {
        return new ExerciseRecordResponse(
                record.getId(),
                record.getExerciseName(),
                record.getDurationMinutes(),
                record.getCaloriesBurned(),
                record.getExerciseType()
        );
    }
}
