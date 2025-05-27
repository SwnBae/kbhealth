package kb.health.dummy;

import kb.health.controller.request.CommentCreateRequest;
import kb.health.controller.request.DietRecordRequest;
import kb.health.controller.request.ExerciseRecordRequest;
import kb.health.controller.request.PostCreateRequest;
import kb.health.controller.request.MemberRegistRequest;
import kb.health.domain.Member;
import kb.health.domain.feed.Post;
import kb.health.domain.record.ExerciseRecord;
import kb.health.domain.record.ExerciseType;
import kb.health.domain.record.MealType;
import kb.health.domain.record.DietRecord;
import kb.health.repository.record.DietRecordRepository;
import kb.health.repository.record.DietRepository;
import kb.health.repository.feed.PostRepository;
import kb.health.repository.record.ExerciseRecordRepository;
import kb.health.repository.MemberRepository;
import kb.health.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class DummyDataService {

    // ===== 의존성 주입 =====
    private final RecordService recordService;
    private final MemberService memberService;
    private final ScoreService scoreService;
    private final FeedService feedService;
    private final DailyNutritionAchievementService dailyNutritionAchievementService;

    private final DietRepository dietRepository;
    private final DietRecordRepository dietRecordRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // ===== 상수 설정 =====
    private final Random random = new Random();

    // 회원 관련 설정
    private static final int MEMBER_COUNT = 20;
    private static final int FOLLOW_CONNECTIONS_PER_MEMBER = 6;

    // 기록 관련 설정
    private static final int MAX_DIET_RECORDS_PER_DAY = 5;
    private static final int MAX_EXERCISE_RECORDS_PER_DAY = 3;
    private static final int DAYS_OF_HISTORY = 5;

    // 피드 관련 설정
    private static final int MIN_POSTS_PER_MEMBER = 2;
    private static final int MAX_POSTS_PER_MEMBER = 5;
    private static final int MIN_COMMENTS_PER_POST = 1;
    private static final int MAX_COMMENTS_PER_POST = 10;
    private static final double LIKE_PROBABILITY = 0.3;

    // 식품 ID 목록
    private static final List<Long> FOOD_IDS = IntStream.rangeClosed(1, 20).boxed().map(Long::valueOf).collect(Collectors.toList());

    // 운동 이름 목록
    private static final List<String> EXERCISE_NAMES = Arrays.asList(
            "달리기", "조깅", "수영", "자전거", "등산", "요가", "필라테스",
            "스쿼트", "런지", "푸시업", "풀업", "플랭크", "벤치프레스",
            "데드리프트", "바벨로우", "숄더프레스", "팔굽혀펴기", "윗몸일으키기"
    );

    /**
     * 무작위 더미 데이터 생성
     */
    public Map<String, Object> generateRandomData() {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            // 1. 무작위 회원 생성
            List<Long> memberIds = createRandomMembers(MEMBER_COUNT);
            result.put("membersCreated", memberIds.size());

            // 2. 무작위 팔로우 관계 설정
            int followCount = createRandomFollowRelationships(memberIds);
            result.put("followRelationshipsCreated", followCount);

            // 3. 무작위 식단 및 운동 기록 생성
            Map<String, Integer> recordCounts = createSimplifiedRandomRecords(memberIds);
            result.put("dietRecordsCreated", recordCounts.get("diet"));
            result.put("exerciseRecordsCreated", recordCounts.get("exercise"));

            // 4. 모든 날짜에 대해 일일 점수를 업데이트
            updateDailyScores();
            result.put("dailyScoresUpdated", true);

            // 5. 무작위 피드(게시글, 댓글, 좋아요) 생성
            Map<String, Integer> feedCounts = createRandomFeeds(memberIds);
            result.put("postsCreated", feedCounts.get("posts"));
            result.put("commentsCreated", feedCounts.get("comments"));
            result.put("likesCreated", feedCounts.get("likes"));

            long endTime = System.currentTimeMillis();
            result.put("success", true);
            result.put("message", "더미 데이터 생성 완료");
            result.put("executionTimeMs", endTime - startTime);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "더미 데이터 생성 중 오류 발생: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * 모든 기존 데이터에 대한 점수, 랭킹, 영양소 달성률 계산
     */
    public Map<String, Object> calculateAllExistingData() {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            // 1. 데이터베이스에서 모든 회원 조회
            List<Member> allMembers = memberRepository.findAll();
            result.put("totalMembers", allMembers.size());

            // 2. 모든 기록의 날짜 범위 계산
            DateRange dateRange = findDateRangeOfAllRecords(allMembers);
            result.put("dateRange", Map.of(
                    "startDate", dateRange.getStartDate().toString(),
                    "endDate", dateRange.getEndDate().toString(),
                    "totalDays", dateRange.getDayCount()
            ));

            // 3. 날짜 범위 내의 모든 날짜에 대해 계산
            Map<String, Integer> processingResult = calculateAllDataForDateRange(dateRange);
            result.put("processedDays", processingResult.get("processedDays"));
            result.put("daysWithData", processingResult.get("daysWithData"));

            long endTime = System.currentTimeMillis();
            result.put("success", true);
            result.put("message", "모든 데이터 계산 완료");
            result.put("executionTimeMs", endTime - startTime);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "데이터 계산 중 오류 발생: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * 최근 30일 데이터에 대한 점수, 랭킹, 영양소 달성률 계산
     */
    public Map<String, Object> calculateLast30DaysData() {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(30);
            DateRange dateRange = new DateRange(startDate, endDate);

            result.put("dateRange", Map.of(
                    "startDate", dateRange.getStartDate().toString(),
                    "endDate", dateRange.getEndDate().toString(),
                    "totalDays", dateRange.getDayCount()
            ));

            Map<String, Integer> processingResult = calculateAllDataForDateRange(dateRange);
            result.put("processedDays", processingResult.get("processedDays"));
            result.put("daysWithData", processingResult.get("daysWithData"));

            long endTime = System.currentTimeMillis();
            result.put("success", true);
            result.put("message", "최근 30일 데이터 계산 완료");
            result.put("executionTimeMs", endTime - startTime);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "최근 30일 데이터 계산 중 오류 발생: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * 시스템 상태 확인
     */
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            // 회원 수 조회
            long memberCount = memberRepository.count();
            status.put("totalMembers", memberCount);

            // 식단 기록 수 조회
            long dietRecordCount = dietRecordRepository.count();
            status.put("totalDietRecords", dietRecordCount);

            // 운동 기록 수 조회
            long exerciseRecordCount = exerciseRecordRepository.count();
            status.put("totalExerciseRecords", exerciseRecordCount);

            // 게시글 수 조회
            long postCount = postRepository.count();
            status.put("totalPosts", postCount);

            status.put("success", true);
            status.put("message", "시스템 상태 조회 완료");

        } catch (Exception e) {
            status.put("success", false);
            status.put("message", "시스템 상태 조회 중 오류 발생: " + e.getMessage());
            status.put("error", e.getClass().getSimpleName());
        }

        return status;
    }

    // ===== 내부 메서드들 (기존 테스트 코드에서 가져온 로직) =====

    private List<Long> createRandomMembers(int count) {
        List<Long> memberIds = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            MemberRegistRequest request = createRandomMemberRequest(i);
            Member member = Member.create(request);
            Long memberId = memberService.save(member);
            memberIds.add(memberId);
        }

        return memberIds;
    }

    private MemberRegistRequest createRandomMemberRequest(int index) {
        MemberRegistRequest request = new MemberRegistRequest();

        request.setAccount("user" + index);
        request.setPassword("password" + index);
        request.setUserName(generateRandomName(index));
        request.setHeight(ThreadLocalRandom.current().nextDouble(150.0, 190.0));
        request.setWeight(ThreadLocalRandom.current().nextDouble(45.0, 100.0));
        request.setGender(random.nextBoolean() ? kb.health.domain.Gender.MALE : kb.health.domain.Gender.FEMALE);
        request.setAge(ThreadLocalRandom.current().nextInt(18, 65));

        return request;
    }

    private String generateRandomName(int index) {
        String[] firstNames = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임", "한", "오", "서", "신", "권", "황", "안", "송", "류", "홍"};
        String[] lastNames = {"민준", "서준", "도윤", "예준", "시우", "하준", "지호", "준서", "준우", "민서",
                "지민", "지우", "지현", "예원", "하은", "다은", "서연", "서현", "민지", "수빈"};

        String baseName = firstNames[random.nextInt(firstNames.length)] + lastNames[random.nextInt(lastNames.length)];
        return baseName + "_" + index;
    }

    private int createRandomFollowRelationships(List<Long> memberIds) {
        int totalFollows = 0;

        for (Long memberId : memberIds) {
            int followCount = ThreadLocalRandom.current().nextInt(0, FOLLOW_CONNECTIONS_PER_MEMBER + 1);

            List<Long> potentialFollowees = memberIds.stream()
                    .filter(id -> !id.equals(memberId))
                    .collect(Collectors.toList());

            Collections.shuffle(potentialFollowees);

            for (int i = 0; i < followCount && i < potentialFollowees.size(); i++) {
                memberService.follow(memberId, potentialFollowees.get(i));
                totalFollows++;
            }
        }

        return totalFollows;
    }

    private Map<String, Integer> createSimplifiedRandomRecords(List<Long> memberIds) {
        LocalDate today = LocalDate.now();
        int totalDietRecords = 0;
        int totalExerciseRecords = 0;

        for (Long memberId : memberIds) {
            int memberDays = Math.min(DAYS_OF_HISTORY, random.nextInt(10) + 5);

            for (int daysAgo = 0; daysAgo < memberDays; daysAgo++) {
                LocalDate recordDate = today.minusDays(daysAgo);

                // 식단 기록 생성
                for (MealType mealType : getRandomMealTypes()) {
                    Long foodId = (long) (random.nextInt(8) + 1);

                    try {
                        int calories = 300 + random.nextInt(500);
                        DietRecordRequest dietRequest = new DietRecordRequest(foodId, calories, null, mealType);
                        recordService.saveDietRecord(dietRequest, memberId, recordDate);
                        totalDietRecords++;
                    } catch (Exception e) {
                        // 오류 무시하고 계속
                    }
                }

                // 운동 기록 생성
                if (random.nextDouble() < 0.3) {
                    try {
                        ExerciseType exerciseType = random.nextBoolean() ?
                                ExerciseType.CARDIO : ExerciseType.WEIGHT;

                        String exerciseName = "운동" + (random.nextInt(5) + 1);
                        int duration = 20 + random.nextInt(40);
                        int caloriesBurned = 200 + random.nextInt(400);

                        ExerciseRecordRequest exRequest = new ExerciseRecordRequest(
                                exerciseName, duration, caloriesBurned, exerciseType, null);

                        Long recordId = recordService.saveExerciseRecord(exRequest, memberId, recordDate);
                        totalExerciseRecords++;

                        if (random.nextDouble() < 0.8) {
                            ExerciseRecord record = recordService.getExerciseRecord(recordId);
                            record.setExercised(true);
                        }
                    } catch (Exception e) {
                        // 오류 무시하고 계속
                    }
                }
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("diet", totalDietRecords);
        result.put("exercise", totalExerciseRecords);
        return result;
    }

    private List<MealType> getRandomMealTypes() {
        List<MealType> allTypes = Arrays.asList(MealType.values());
        Collections.shuffle(allTypes);

        int count = random.nextInt(3) + 1;
        return allTypes.subList(0, Math.min(count, allTypes.size()));
    }

    private void updateDailyScores() {
        LocalDate today = LocalDate.now();

        for (int daysAgo = DAYS_OF_HISTORY - 1; daysAgo >= 0; daysAgo--) {
            LocalDate scoreDate = today.minusDays(daysAgo);

            try {
                scoreService.updateDailyScoresForAllMembers(scoreDate);
            } catch (Exception e) {
                // 오류 무시하고 계속
            }
        }
    }

    private Map<String, Integer> createRandomFeeds(List<Long> memberIds) {
        List<Long> postIds = createRandomPosts(memberIds);
        int commentCount = 0;
        int likeCount = 0;

        if (!postIds.isEmpty()) {
            commentCount = createRandomComments(memberIds, postIds);
            likeCount = createRandomLikes(memberIds, postIds);
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("posts", postIds.size());
        result.put("comments", commentCount);
        result.put("likes", likeCount);
        return result;
    }

    private List<Long> createRandomPosts(List<Long> memberIds) {
        for (Long memberId : memberIds) {
            int postCount = ThreadLocalRandom.current().nextInt(MIN_POSTS_PER_MEMBER, MAX_POSTS_PER_MEMBER + 1);

            for (int i = 0; i < postCount; i++) {
                try {
                    // 무작위 제목과 내용 생성
                    String title = generateRandomPostTitle();
                    String content = generateRandomPostContent();

                    // 이미지 URL (80%의 확률로 null, 20%의 확률로 가상 URL)
                    String imageUrl = random.nextDouble() < 0.8 ? null :
                            "https://example.com/images/health_" + random.nextInt(1000) + ".jpg";

                    PostCreateRequest request = new PostCreateRequest();
                    request.setTitle(title);
                    request.setContent(content);

                    feedService.savePost(memberId, request, imageUrl);
                } catch (Exception e) {
                    // 오류 무시하고 계속
                }
            }
        }

        List<Post> allPosts = postRepository.findAll();
        return allPosts.stream().map(Post::getId).collect(Collectors.toList());
    }

    private int createRandomComments(List<Long> memberIds, List<Long> postIds) {
        int totalComments = 0;

        for (Long postId : postIds) {
            int commentCount = ThreadLocalRandom.current().nextInt(MIN_COMMENTS_PER_POST, MAX_COMMENTS_PER_POST + 1);

            for (int i = 0; i < commentCount; i++) {
                try {
                    Long commenterId = memberIds.get(random.nextInt(memberIds.size()));
                    String commentText = generateRandomCommentText();

                    CommentCreateRequest request = new CommentCreateRequest();
                    request.setComment(commentText);

                    feedService.saveComment(commenterId, postId, request);
                    totalComments++;
                } catch (Exception e) {
                    // 오류 무시하고 계속
                }
            }
        }

        return totalComments;
    }

    private int createRandomLikes(List<Long> memberIds, List<Long> postIds) {
        int totalLikes = 0;

        for (Long memberId : memberIds) {
            for (Long postId : postIds) {
                if (random.nextDouble() < LIKE_PROBABILITY) {
                    try {
                        boolean isLiked = feedService.postLikeToggle(memberId, postId);
                        if (isLiked) {
                            totalLikes++;
                        }
                    } catch (Exception e) {
                        // 오류 무시하고 계속
                    }
                }
            }
        }

        return totalLikes;
    }

    private Map<String, Integer> calculateAllDataForDateRange(DateRange dateRange) {
        LocalDate currentDate = dateRange.getStartDate();
        int processedDays = 0;
        int daysWithData = 0;

        while (!currentDate.isAfter(dateRange.getEndDate())) {
            // 1. 랭킹 갱신
            memberService.updateMemberRankingsForDate(currentDate);

            // 2. 점수 계산
            boolean hasData = calculateScoreForDate(currentDate);
            processedDays++;

            if (hasData) {
                daysWithData++;

                // 3. 영양소 달성률 계산
                calculateNutritionAchievementForDate(currentDate);
            }

            currentDate = currentDate.plusDays(1);
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("processedDays", processedDays);
        result.put("daysWithData", daysWithData);
        return result;
    }

    private boolean calculateScoreForDate(LocalDate date) {
        try {
            scoreService.updateDailyScoresForAllMembers(date);
            return checkIfDateHasRecords(date);
        } catch (Exception e) {
            return false;
        }
    }

    private void calculateNutritionAchievementForDate(LocalDate date) {
        try {
            dailyNutritionAchievementService.updateDailyNutritionAchievementsForAllMembers(date);
        } catch (Exception e) {
            // 오류 무시하고 계속
        }
    }

    private boolean checkIfDateHasRecords(LocalDate date) {
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            List<DietRecord> dietRecords = findDietRecordsByMemberAndDate(member, date);
            List<ExerciseRecord> exerciseRecords = findExerciseRecordsByMemberAndDate(member, date);

            if (!dietRecords.isEmpty() || !exerciseRecords.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private List<DietRecord> findDietRecordsByMemberAndDate(Member member, LocalDate date) {
        LocalDateTime start = date.atStartOfDay().minusDays(1);
        LocalDateTime end = date.atStartOfDay();
        return dietRecordRepository.findByMemberAndDateRange(member, start, end);
    }

    private List<ExerciseRecord> findExerciseRecordsByMemberAndDate(Member member, LocalDate date) {
        LocalDateTime start = date.atStartOfDay().minusDays(1);
        LocalDateTime end = date.atStartOfDay();
        return exerciseRecordRepository.findByMemberAndDateRange(member, start, end);
    }

    private DateRange findDateRangeOfAllRecords(List<Member> members) {
        LocalDate earliestDate = LocalDate.now();
        LocalDate latestDate = LocalDate.of(2000, 1, 1);

        Set<LocalDate> allDates = new HashSet<>();

        for (Member member : members) {
            List<DietRecord> dietRecords = dietRecordRepository.findByMemberId(member.getId());
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

            List<ExerciseRecord> exerciseRecords = exerciseRecordRepository.findByMemberId(member.getId());
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

        if (allDates.isEmpty()) {
            return new DateRange(LocalDate.now(), LocalDate.now());
        }

        return new DateRange(earliestDate, latestDate);
    }

    // ===== 콘텐츠 생성 관련 메서드 =====
    /**
     * 무작위 게시글 제목을 생성하는 메서드
     * @return 생성된 제목
     */
    private String generateRandomPostTitle() {
        // 게시글 제목 템플릿
        String[] titleTemplates = {
                "오늘의 운동 기록 #%d",
                "다이어트 %d일차 느낌",
                "건강식 레시피 공유 - %s 만들기",
                "%s에서 운동하는 날",
                "체중 감량 %d주차 성과",
                "오늘의 식단 인증!",
                "운동 목표 달성 %d%%!",
                "%s 챌린지 동참합니다",
                "건강 관리 팁 - %s 효과",
                "오늘의 건강 스트레칭 #%d",
                "체력 단련 %d주 후기",
                "홈트레이닝 루틴 공유",
                "%s 식단으로 건강 챙기기",
                "일주일 동안 %s만 먹어보았다",
                "운동 기구 없이 할 수 있는 %s 운동",
                "식단 관리의 중요성",
                "건강검진 결과 공유",
                "금주 %d일차 도전기",
                "아침 운동의 효과",
                "%s 효과 직접 체험해봤습니다"
        };

        // 무작위 제목 템플릿 선택
        String template = titleTemplates[random.nextInt(titleTemplates.length)];

        // 템플릿에 따라 적절한 포맷 적용
        if (template.contains("%d")) {
            return String.format(template, random.nextInt(100) + 1);
        } else if (template.contains("%s")) {
            String[] fillers = {"단백질", "채소", "과일", "공원", "헬스장", "집", "아침", "저녁", "유산소", "근력", "요가", "필라테스",
                    "케톤", "저탄수", "고단백", "간헐적 단식", "물", "달리기", "명상", "스트레칭"};
            return String.format(template, fillers[random.nextInt(fillers.length)]);
        } else {
            return template;
        }
    }

    /**
     * 무작위 게시글 내용을 생성하는 메서드
     * @return 생성된 내용
     */
    private String generateRandomPostContent() {
        // 기본 문장 목록
        String[] sentences = {
                "오늘도 열심히 운동했습니다!",
                "건강한 식습관을 유지하는 것이 중요해요.",
                "목표 달성을 위해 하루하루 노력 중입니다.",
                "오늘 먹은 음식들은 모두 영양가 있는 것들이었어요.",
                "운동 후 단백질 보충은 필수입니다!",
                "건강한 몸을 위해 꾸준히 관리하고 있어요.",
                "체중 감량보다 건강한 생활습관이 더 중요합니다.",
                "요즘 새로운 운동 루틴을 시작했어요.",
                "건강 관리에 관심이 많아지니 몸 상태가 좋아지는 것이 느껴집니다.",
                "오늘의 식단을 공유합니다. 여러분도 함께해요!",
                "건강 관리는 꾸준함이 핵심이라고 생각해요.",
                "운동과 함께 충분한 휴식도 중요하답니다.",
                "올바른 자세로 운동하는 것이 무엇보다 중요해요.",
                "균형 잡힌 식단을 위해 영양소를 고루 섭취하려고 노력 중입니다.",
                "좋은 습관을 만들어가는 과정이 때로는 힘들지만 보람차요.",
                "다양한 식재료로 건강하게 먹는 것이 중요해요.",
                "몸과 마음의 건강은 서로 연결되어 있다고 생각합니다.",
                "오늘은 특별히 유산소 운동에 집중했습니다.",
                "근력 운동을 통해 기초 대사량을 높이는 것이 도움이 됩니다.",
                "적절한 수분 섭취는 건강 관리의 기본입니다.",
                "충분한 수면은 건강한 생활의 핵심이라고 생각해요.",
                "스트레스 관리도 건강 관리의 중요한 부분입니다.",
                "매일 조금씩 꾸준히 하는 것이 비결이에요.",
                "건강한 간식 선택으로 하루 중 에너지를 유지하세요.",
                "저염식 습관이 건강에 큰 도움이 됩니다."
        };

        // 무작위로 2~5개의 문장 선택
        int sentenceCount = ThreadLocalRandom.current().nextInt(2, 6);
        StringBuilder content = new StringBuilder();
        Set<Integer> selectedIndices = new HashSet<>();

        // 문장 중복 없이 조합
        while (selectedIndices.size() < sentenceCount) {
            int index = random.nextInt(sentences.length);
            if (!selectedIndices.contains(index)) {
                selectedIndices.add(index);
                content.append(sentences[index]).append(" ");
            }
        }

        // 해시태그 추가 (70% 확률)
        if (random.nextDouble() < 0.7) {
            String[] hashtags = {
                    "#건강", "#다이어트", "#운동", "#헬스", "#건강식", "#식단", "#건강관리", "#헬시라이프", "#운동스타그램",
                    "#홈트레이닝", "#웨이트", "#요가", "#필라테스", "#러닝", "#영양", "#체중관리", "#헬스케어", "#단백질",
                    "#근력운동", "#유산소", "#수분섭취", "#균형잡힌식단", "#매일운동", "#건강한하루", "#피트니스"
            };

            // 1~4개의 해시태그 추가
            int hashtagCount = ThreadLocalRandom.current().nextInt(1, 5);
            Set<Integer> selectedHashtags = new HashSet<>();

            for (int i = 0; i < hashtagCount; i++) {
                int index = random.nextInt(hashtags.length);
                if (!selectedHashtags.contains(index)) {
                    selectedHashtags.add(index);
                    content.append(hashtags[index]).append(" ");
                }
            }
        }

        return content.toString().trim();
    }

    /**
     * 무작위 댓글 내용을 생성하는 메서드
     * @return 생성된 댓글 내용
     */
    private String generateRandomCommentText() {
        // 댓글 템플릿
        String[] commentTemplates = {
                "멋져요! 응원합니다!",
                "저도 도전해보고 싶네요.",
                "정말 인상적인 성과네요! 축하해요.",
                "어떤 방법으로 관리하시는지 더 자세히 알고 싶어요!",
                "건강 관리 정말 잘 하고 계시네요!",
                "좋은 정보 감사합니다.",
                "저도 같이 도전해 볼게요!",
                "꾸준함이 정말 중요한 것 같아요.",
                "운동 루틴이 궁금해요!",
                "식단 관리도 중요하죠. 잘 하고 계세요!",
                "건강한 하루 보내세요!",
                "정말 동기부여가 됩니다.",
                "오늘도 화이팅!",
                "좋은 팁 감사합니다.",
                "계속 응원할게요!",
                "저도 비슷한 방법으로 시도해보고 있어요!",
                "효과가 정말 좋아 보이네요!",
                "영감을 받았습니다. 감사해요.",
                "건강한 습관이 중요하죠. 잘 실천하고 계시네요!",
                "꾸준히 하시는 모습이 정말 멋져요.",
                "어떤 효과를 느끼셨나요?",
                "저도 함께 노력해볼게요!",
                "건강 관리의 좋은 예시네요!",
                "이런 정보 더 많이 공유해주세요!",
                "정말 열심히 하시는 것 같아요!",
                "식단 레시피도 공유해주세요!",
                "훌륭한 성과네요! 대단해요.",
                "앞으로도 좋은 결과 있으시길 바랍니다.",
                "건강한 도전에 박수를 보냅니다!",
                "매일 조금씩이 중요하죠. 응원합니다!"
        };

        return commentTemplates[random.nextInt(commentTemplates.length)];
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