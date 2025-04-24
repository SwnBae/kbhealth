package kb.health.domain.record;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class ExerciseRecord extends BaseRecord {

    private int durationMinutes;
    private int caloriesBurned;

    @Enumerated(EnumType.STRING)
    private ExerciseType exerciseType;
}
