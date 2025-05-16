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
    private String exerciseName;

    @Column(nullable = false)
    private int durationMinutes;

    @Column(nullable = false)
    private int caloriesBurned;

    @Enumerated(EnumType.STRING)
    private ExerciseType exerciseType;

    private boolean exercised;

    private String erImgUrl;

    /* 빌더 */
    public static ExerciseRecord create(int durationMinutes, int caloriesBurned, String erImgUrl, ExerciseType exerciseType) {
        ExerciseRecord record = new ExerciseRecord();
        record.setDurationMinutes(durationMinutes);
        record.setCaloriesBurned(caloriesBurned);
        record.setExerciseType(exerciseType);
        record.setErImgUrl(erImgUrl);

        return record;
    }

    public static ExerciseRecord create(ExerciseRecordRequest request) {
        ExerciseRecord record = new ExerciseRecord();
        record.setExerciseName(request.getExerciseName());
        record.setDurationMinutes(request.getDurationMinutes());
        record.setCaloriesBurned(request.getCaloriesBurned());
        record.setExerciseType(request.getExerciseType());

        if (request.getErImgUrl() != null) {
            record.setErImgUrl(request.getErImgUrl());
        } else {
            switch (request.getExerciseType()) {
                case CARDIO:
                    record.setErImgUrl("/images/default_cardio.png");
                    break;
                case WEIGHT:
                    record.setErImgUrl("/images/default_weight.png");
                    break;
                case YOGA:
                    record.setErImgUrl("/images/default_yoga.png");
                    break;
                case SWIMMING:
                    record.setErImgUrl("/images/default_swim.png");
                    break;
            }
        }

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
