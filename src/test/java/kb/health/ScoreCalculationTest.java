package kb.health;

import kb.health.domain.Member;
import kb.health.domain.record.DietRecord;
import kb.health.domain.record.ExerciseRecord;
import kb.health.repository.MemberRepository;
import kb.health.repository.RecordRepository;
import kb.health.service.ScoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 이미 저장된 테스트 데이터의 점수를 계산하는 테스트 클래스
 * 이 테스트는 데이터베이스에 있는 모든 기록에 대해 점수를 새로 계산합니다.
 */
@SpringBootTest
@Transactional
public class ScoreCalculationTest {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private MemberRepository memberRepository;

    /**
     * 모든 기록 데이터에 대해 점수를 계산합니다.
     */
    @Test
    @Rollback(false) // 변경사항을 데이터베이스에 저장
    public void calculateScoresForAllExistingData() {
        System.out.println("=== 모든 기존 데이터에 대한 점수 계산 시작 ===");

        // 1. 데이터베이스에서 모든 회원 조회
        List<Member> allMembers = memberRepository.findAll();
        System.out.println("총 " + allMembers.size() + "명의 회원 데이터를 처리합니다.");

        // 2. 모든 기록의 날짜 범위 계산
        DateRange dateRange = findDateRangeOfAllRecords(allMembers);
        System.out.println("기록 날짜 범위: " + dateRange.getStartDate() + " ~ " + dateRange.getEndDate());

        // 3. 날짜 범위 내의 모든 날짜에 대해 점수 계산
        calculateScoresForDateRange(dateRange);

        System.out.println("=== 점수 계산 완료 ===");
    }

    /**
     * 최근 30일간의 데이터에 대해서만 점수를 계산합니다.
     */
    @Test
    @Rollback(false)
    public void calculateScoresForLast30Days() {
        System.out.println("=== 최근 30일 데이터에 대한 점수 계산 시작 ===");

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        DateRange dateRange = new DateRange(startDate, endDate);

        System.out.println("기록 날짜 범위: " + dateRange.getStartDate() + " ~ " + dateRange.getEndDate());

        // 날짜 범위 내의 모든 날짜에 대해 점수 계산
        calculateScoresForDateRange(dateRange);

        System.out.println("=== 점수 계산 완료 ===");
    }

    /**
     * 특정 날짜 범위 내의 모든 날짜에 대해 점수를 계산합니다.
     */
    private void calculateScoresForDateRange(DateRange dateRange) {
        LocalDate currentDate = dateRange.getStartDate();
        int processedDays = 0;
        int daysWithData = 0;

        while (!currentDate.isAfter(dateRange.getEndDate())) {
            System.out.println("날짜 " + currentDate + " 처리 중...");
            boolean hasData = calculateScoreForDate(currentDate);
            processedDays++;

            if (hasData) {
                daysWithData++;
            }

            currentDate = currentDate.plusDays(1);
        }

        System.out.println("총 " + processedDays + "일 처리 완료, 그 중 " + daysWithData + "일에 데이터가 있었습니다.");
    }

    /**
     * 특정 날짜의 점수를 계산합니다.
     * @return 해당 날짜에 데이터가 있었는지 여부
     */
    private boolean calculateScoreForDate(LocalDate date) {
        try {
            // 모든 회원에 대해 해당 날짜의 점수 계산
            scoreService.updateDailyScoresForAllMembers(date);
            // 일부 회원에 대해서만 데이터가 있을 수 있으므로 모든 회원 데이터를 확인
            boolean hasData = checkIfDateHasRecords(date);
            if (hasData) {
                System.out.println("  - " + date + ": 점수 계산 완료 (데이터 있음)");
            } else {
                System.out.println("  - " + date + ": 점수 계산 완료 (데이터 없음)");
            }
            return hasData;
        } catch (Exception e) {
            System.err.println("날짜 " + date + "의 점수 계산 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 특정 날짜에 기록이 있는지 확인합니다.
     */
    private boolean checkIfDateHasRecords(LocalDate date) {
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            List<DietRecord> dietRecords = recordRepository.findDietRecordsByMemberAndDate(member, date);
            List<ExerciseRecord> exerciseRecords = recordRepository.findExerciseRecordsByMemberAndDate(member, date);

            if (!dietRecords.isEmpty() || !exerciseRecords.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 모든 회원의 모든 기록에서 날짜 범위를 찾습니다.
     */
    private DateRange findDateRangeOfAllRecords(List<Member> members) {
        LocalDate earliestDate = LocalDate.now();
        LocalDate latestDate = LocalDate.of(2000, 1, 1); // 충분히 오래된 날짜로 초기화

        Set<LocalDate> allDates = new HashSet<>();

        // 모든 회원의 모든 기록 날짜 수집
        for (Member member : members) {
            // 식단 기록 날짜 수집
            List<DietRecord> dietRecords = recordRepository.findDietRecordsByMember(member.getId());
            for (DietRecord record : dietRecords) {
                LocalDate recordDate = record.getCreatedDate().toLocalDate();
                allDates.add(recordDate);

                if (recordDate.isBefore(earliestDate)) {
                    earliestDate = recordDate;
                }
                if (recordDate.isAfter(latestDate)) {
                    latestDate = recordDate;
                }
            }

            // 운동 기록 날짜 수집
            List<ExerciseRecord> exerciseRecords = recordRepository.findExerciseRecordsByMember(member.getId());
            for (ExerciseRecord record : exerciseRecords) {
                LocalDate recordDate = record.getCreatedDate().toLocalDate();
                allDates.add(recordDate);

                if (recordDate.isBefore(earliestDate)) {
                    earliestDate = recordDate;
                }
                if (recordDate.isAfter(latestDate)) {
                    latestDate = recordDate;
                }
            }
        }

        System.out.println("기록이 있는 고유한 날짜 수: " + allDates.size());

        // 날짜가 없으면 현재 날짜를 사용
        if (allDates.isEmpty()) {
            return new DateRange(LocalDate.now(), LocalDate.now());
        }

        return new DateRange(earliestDate, latestDate);
    }

    /**
     * 날짜 범위를 저장하는 내부 클래스
     */
    private static class DateRange {
        private final LocalDate startDate;
        private final LocalDate endDate;

        public DateRange(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public long getDayCount() {
            return ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
    }
}