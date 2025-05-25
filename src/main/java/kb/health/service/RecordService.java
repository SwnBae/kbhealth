package kb.health.service;

import kb.health.repository.*;
import kb.health.controller.response.NutritionAchievementResponse;
import kb.health.domain.DailyNutritionStandard;
import kb.health.domain.Member;
import kb.health.domain.record.*;
import kb.health.controller.request.DietRecordRequest;
import kb.health.controller.request.DietRequest;
import kb.health.controller.request.ExerciseRecordRequest;
import kb.health.repository.record.DietRecordRepository;
import kb.health.repository.record.DietRepository;
import kb.health.repository.record.ExerciseRecordRepository;
import kb.health.repository.record.RecordRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecordService {

    private final MemberRepository memberRepository;
    private final DietRepository dietRepository;
    private final DietRecordRepository dietRecordRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final RecordRepositoryCustom recordRepositoryCustom;

    /**
     * 음식 관리
     */
    @Transactional
    public Long addDiet(DietRequest dietRequest) {
        Diet diet = new Diet();
        diet.setMenu(dietRequest.getMenu());
        diet.setCalories(dietRequest.getCalories());

        return dietRepository.save(diet).getId();
    }

    @Transactional
    public void updateDiet(Long dietId, DietRequest dietRequest) {
        Diet diet = dietRepository.findById(dietId).orElse(null);

        diet.setMenu(dietRequest.getMenu());
        diet.setCalories(dietRequest.getCalories());
    }

    @Transactional
    public void deleteDiet(Long dietId) {
        Diet diet = dietRepository.findById(dietId).orElse(null);

        if (diet != null) {
            List<DietRecord> dietRecords = dietRecordRepository.findByDiet(diet);
            for (DietRecord dietRecord : dietRecords) {
                dietRecord.setDiet(null);
            }

            dietRepository.deleteById(dietId);
        }
    }

    public Diet getDiet(Long id){
        return dietRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Diet not found with id: " + id));
    }

    public List<Diet> getDietList(){
        return dietRepository.findAll();
    }

    /**
     * Record 관리
     */

    /**
     * 저장
     */
    @Transactional
    public Long saveDietRecord(DietRecordRequest dietRecordRequest, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Diet diet = dietRepository.findById(dietRecordRequest.getDietId())
                .orElseThrow(() -> new IllegalArgumentException("Diet not found with id: " + dietRecordRequest.getDietId()));

        DietRecord dietRecord = DietRecord.create(diet, dietRecordRequest.getAmount(), dietRecordRequest.getDrImgUrl(), dietRecordRequest.getMealType());
        dietRecord.assignMember(member);

        return dietRecordRepository.save(dietRecord).getId();
    }

    @Transactional
    public Long saveExerciseRecord(ExerciseRecordRequest exerciseRecordRequest, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        ExerciseRecord exerciseRecord = ExerciseRecord.create(exerciseRecordRequest);

        exerciseRecord.assignMember(member);

        return exerciseRecordRepository.save(exerciseRecord).getId();
    }

    /**
     * 테스트 코드
     */
    @Transactional
    public Long saveDietRecord(DietRecordRequest dietRecordRequest, Long memberId, LocalDate customDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Diet diet = dietRepository.findById(dietRecordRequest.getDietId())
                .orElseThrow(() -> new IllegalArgumentException("Diet not found with id: " + dietRecordRequest.getDietId()));;

        DietRecord dietRecord = DietRecord.create(diet, dietRecordRequest.getAmount(), dietRecordRequest.getDrImgUrl(), dietRecordRequest.getMealType());
        dietRecord.assignMember(member);

        // Set custom date
        LocalDateTime customDateTime = customDate.atStartOfDay();
        dietRecord.setCreatedDate(customDateTime);
        dietRecord.setLastModifyDate(customDateTime);

        return dietRecordRepository.save(dietRecord).getId();
    }

    @Transactional
    public Long saveExerciseRecord(ExerciseRecordRequest exerciseRecordRequest, Long memberId, LocalDate customDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        ExerciseRecord exerciseRecord = ExerciseRecord.create(exerciseRecordRequest);
        exerciseRecord.assignMember(member);

        LocalDateTime customDateTime = customDate.atStartOfDay();
        exerciseRecord.setCreatedDate(customDateTime);  // Set the custom createdDate
        exerciseRecord.setLastModifyDate(customDateTime);  // Set the custom lastModifyDate

        return exerciseRecordRepository.save(exerciseRecord).getId();
    }
    /**
     * 테스트 코드 끝
     */

    public List<DietRecord> getDietRecords(Long memberId) {
        return dietRecordRepository.findByMemberId(memberId);
    }

    public List<ExerciseRecord> getExerciseRecords(Long memberId) {
        return exerciseRecordRepository.findByMemberId(memberId);
    }

    public DietRecord getDietRecord(Long id) {
        return dietRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DietRecord not found with id: " + id));
    }

    public ExerciseRecord getExerciseRecord(Long id) {
        return exerciseRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ExerciseRecord not found with id: " + id));
    }

    /**
     * 삭제
     */
    @Transactional
    public void deleteDietRecord(Long currentMemberId, Long id) {
        DietRecord dietRecord = dietRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DietRecord not found with id: " + id));

        if (!dietRecord.getMember().getId().equals(currentMemberId)) {
//            throw new AuthorizationException("본인의 기록만 수정할 수 있습니다");
        }

        dietRecord.deleteFromMember();
        dietRecordRepository.deleteById(id);
    }

    @Transactional
    public void deleteExerciseRecord(Long currentMemberId, Long id) {
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ExerciseRecord not found with id: " + id));

        if (!exerciseRecord.getMember().getId().equals(currentMemberId)) {
//            throw new AuthorizationException("본인의 기록만 수정할 수 있습니다");
        }

        exerciseRecord.deleteFromMember();
        exerciseRecordRepository.deleteById(id);
    }

    /**
     * 수정
     */
    @Transactional
    public void updateDietRecord(Long currentMemberId, Long id, DietRecordRequest dietRecordRequest){
        DietRecord dietRecord = dietRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DietRecord not found with id: " + id));

        if (!dietRecord.getMember().getId().equals(currentMemberId)) {
//            throw new AuthorizationException("본인의 기록만 수정할 수 있습니다");
        }

        Diet diet = dietRepository.findById(dietRecordRequest.getDietId())
                .orElseThrow(() -> new IllegalArgumentException("Diet not found with id: " + dietRecordRequest.getDietId()));;

        dietRecord.setDiet(diet);
        dietRecord.setAmount(dietRecordRequest.getAmount());
        dietRecord.setMealType(dietRecordRequest.getMealType());

        if(dietRecordRequest.getDrImgUrl() != null) {
            dietRecord.setDrImgUrl(dietRecordRequest.getDrImgUrl());
        } else{
            dietRecord.setDrImgUrl("/images/default_food.png");
        }
    }

    @Transactional
    public void updateExerciseRecord(Long currentMemberId, Long id, ExerciseRecordRequest exerciseRecordRequest){
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ExerciseRecord not found with id: " + id));

        if (!exerciseRecord.getMember().getId().equals(currentMemberId)) {
//            throw new AuthorizationException("본인의 기록만 수정할 수 있습니다");
        }

        exerciseRecord.setExerciseName(exerciseRecordRequest.getExerciseName());
        exerciseRecord.setDurationMinutes(exerciseRecordRequest.getDurationMinutes());
        exerciseRecord.setCaloriesBurned(exerciseRecordRequest.getCaloriesBurned());

        if (exerciseRecordRequest.getErImgUrl() != null) {
            exerciseRecord.setErImgUrl(exerciseRecordRequest.getErImgUrl());
        } else {
            switch (exerciseRecordRequest.getExerciseType()) {
                case CARDIO:
                    exerciseRecord.setErImgUrl("/images/default_cardio.png");
                    break;
                case WEIGHT:
                    exerciseRecord.setErImgUrl("/images/default_weight.png");
                    break;
                case YOGA:
                    exerciseRecord.setErImgUrl("/images/default_yoga.png");
                    break;
                case SWIMMING:
                    exerciseRecord.setErImgUrl("/images/default_swim.png");
                    break;
            }
        }
    }

    /**
     * 운동 완료 표시
     */
    @Transactional
    public void markExerciseAsCompleted(Long memberId, Long exId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        ExerciseRecord record = exerciseRecordRepository.findById(exId)
                .orElseThrow(() -> new IllegalArgumentException("운동 기록을 찾을 수 없습니다."));

        if (!record.getMember().getId().equals(member.getId())) {
            throw new IllegalStateException("현재 사용자와 기록의 소유자가 일치하지 않습니다.");
        }

        if (record.isExercised()) {
            throw new IllegalStateException("이미 완료된 운동입니다.");
        }

        record.setExercised(true);
    }

    @Transactional
    public void unmarkExerciseAsCompleted(Long memberId, Long exId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        ExerciseRecord record = exerciseRecordRepository.findById(exId)
                .orElseThrow(() -> new IllegalArgumentException("운동 기록을 찾을 수 없습니다."));

        if (!record.getMember().getId().equals(member.getId())) {
            throw new IllegalStateException("현재 사용자와 기록의 소유자가 일치하지 않습니다.");
        }

        if (!record.isExercised()) {
            throw new IllegalStateException("아직 완료되지 않은 운동입니다.");
        }

        record.setExercised(false);
    }

    /**
     * 검색
     */
    public List<DietRecord> searchDietRecordsDynamic(Long memberId, String menuKeyword, LocalDate startDate, LocalDate endDate) {
        return recordRepositoryCustom.searchDietRecordsDynamic(memberId, menuKeyword, startDate, endDate);
    }

    public List<ExerciseRecord> searchExerciseRecordsDynamic(Long memberId, String exerciseName, LocalDate startDate, LocalDate endDate) {
        return recordRepositoryCustom.searchExerciseRecordsDynamic(memberId, exerciseName, startDate, endDate);
    }

    public List<Diet> searchDietsByMenu(String keyword) {
        return dietRepository.findByMenuContaining(keyword);
    }

    /**
     * 하루 영양소 달성률
     * 조회할 때마다 계산해서 반환한다 -> 멤버 신체정보 업데이트 해도 정상적으로 반환된다.
     */
    public NutritionAchievementResponse getNutritionAchievement(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        List<DietRecord> records = findDietRecordsByMemberAndDateOnly(member, date);
        DailyNutritionStandard standard = member.getDailyNutritionStandard();
        return NutritionAchievementResponse.create(records, standard);
    }

    // 날짜 범위 조회 헬퍼 메서드
    private List<DietRecord> findDietRecordsByMemberAndDateOnly(Member member, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return dietRecordRepository.findByMemberAndDateOnly(member, start, end);
    }
}
