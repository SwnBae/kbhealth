package kb.health.controller.response;

import kb.health.domain.record.ExerciseRecord;
import kb.health.domain.record.ExerciseType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ExerciseRecordResponse {
    private Long id;
    private String exerciseName;
    private int durationMinutes;
    private int caloriesBurned;
    private LocalDateTime lastModifyDate;
    private ExerciseType exerciseType;
    private boolean isExercised;

    public static ExerciseRecordResponse create(ExerciseRecord record) {
        return new ExerciseRecordResponse(
                record.getId(),
                record.getExerciseName(),
                record.getDurationMinutes(),
                record.getCaloriesBurned(),
                record.getLastModifyDate(),
                record.getExerciseType(),
                record.isExercised()
        );
    }
}
