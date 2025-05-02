package kb.health.domain.record;

import jakarta.persistence.*;
import kb.health.domain.Member;
import kb.health.controller.request.ExerciseRecordRequest;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class ExerciseRecord extends BaseRecord {

    @Id
    @GeneratedValue
    @Column(name = "exercise_record_id")
    private Long id;

    @Column(nullable = false)
    private int durationMinutes;

    @Column(nullable = false)
    private int caloriesBurned;

    @Enumerated(EnumType.STRING)
    private ExerciseType exerciseType;

    private boolean exercised;

    /* 빌더 */
    public static ExerciseRecord create(int durationMinutes, int caloriesBurned, ExerciseType exerciseType) {
        ExerciseRecord record = new ExerciseRecord();
        record.setDurationMinutes(durationMinutes);
        record.setCaloriesBurned(caloriesBurned);
        record.setExerciseType(exerciseType);

        return record;
    }

    public static ExerciseRecord create(ExerciseRecordRequest request) {
        ExerciseRecord record = new ExerciseRecord();
        record.setDurationMinutes(request.getDurationMinutes());
        record.setCaloriesBurned(request.getCaloriesBurned());
        record.setExerciseType(request.getExerciseType());

        return record;
    }

    /* 연관관계 편의 메서드 */
    public void assignMember(Member member) {
        this.setMember(member);
        member.getExerciseRecords().add(this);
    }

    public void deleteFromMember() {
        if (this.getMember() != null) {
            this.getMember().getExerciseRecords().remove(this);
            this.setMember(null);
        }
    }
}
