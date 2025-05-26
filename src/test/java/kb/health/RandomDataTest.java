package kb.health;

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
import kb.health.repository.record.DietRecordRepository;
import kb.health.repository.record.DietRepository;
import kb.health.repository.feed.PostRepository;
import kb.health.repository.record.ExerciseRecordRepository;
import kb.health.service.FeedService;
import kb.health.service.MemberService;
import kb.health.service.RecordService;
import kb.health.service.ScoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 무작위 회원, 팔로우 관계, 식단, 운동, 피드 데이터를 생성하는 테스트 클래스
 */
@SpringBootTest
@Transactional
public class RandomDataTest {

    // ===== 의존성 주입 =====
    @Autowired
    RecordService recordService;

    @Autowired
    MemberService memberService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    FeedService feedService;

    @Autowired
    DietRepository dietRepository;

    @Autowired
    DietRecordRepository dietRecordRepository;

    @Autowired
    ExerciseRecordRepository exerciseRecordRepository;

    @Autowired
    PostRepository postRepository;

    // ===== 상수 설정 =====
    private final Random random = new Random();

    // 회원 관련 설정
    private static final int MEMBER_COUNT = 50;
    private static final int FOLLOW_CONNECTIONS_PER_MEMBER = 20; // 각 회원당 평균 팔로우 수

    // 기록 관련 설정
    private static final int MAX_DIET_RECORDS_PER_DAY = 5;
    private static final int MAX_EXERCISE_RECORDS_PER_DAY = 3;
    private static final int DAYS_OF_HISTORY = 5; // 몇 일 동안의 기록을 생성할 것인지

    // 피드 관련 설정
    private static final int MIN_POSTS_PER_MEMBER = 2; // 각 회원당 최소 게시글 수
    private static final int MAX_POSTS_PER_MEMBER = 5; // 각 회원당 최대 게시글 수
    private static final int MIN_COMMENTS_PER_POST = 1; // 각 게시글당 최소 댓글 수
    private static final int MAX_COMMENTS_PER_POST = 10; // 각 게시글당 최대 댓글 수
    private static final double LIKE_PROBABILITY = 0.3; // 게시글에 좋아요할 확률

    // 식품 ID 목록 (실제 데이터베이스의 식품 ID에 맞게 조정 필요)
    private static final List<Long> FOOD_IDS = IntStream.rangeClosed(1, 20).boxed().map(Long::valueOf).collect(Collectors.toList());

    // 운동 이름 목록
    private static final List<String> EXERCISE_NAMES = Arrays.asList(
            "달리기", "조깅", "수영", "자전거", "등산", "요가", "필라테스",
            "스쿼트", "런지", "푸시업", "풀업", "플랭크", "벤치프레스",
            "데드리프트", "바벨로우", "숄더프레스", "팔굽혀펴기", "윗몸일으키기"
    );

    // ===== 메인 테스트 메서드 =====
    /**
     * 무작위 데이터를 생성하는 메인 테스트 메서드
     */
    @Test
    @Rollback(false) // 테스트 후 롤백하지 않음 (실제 DB에 데이터 저장)
    public void generateRandomData() {
        System.out.println("무작위 데이터 생성 시작...");

        try {
            // 1. 무작위 회원 생성
            List<Long> memberIds = createRandomMembers(MEMBER_COUNT);
            System.out.println(memberIds.size() + "명의 무작위 회원 생성 완료");

            // 2. 무작위 팔로우 관계 설정
            createRandomFollowRelationships(memberIds);
            System.out.println("무작위 팔로우 관계 설정 완료");

            // 3. 무작위 식단 및 운동 기록 생성
            createSimplifiedRandomRecords(memberIds);
            System.out.println("무작위 식단 및 운동 기록 생성 완료");

            // 4. 모든 날짜에 대해 일일 점수를 업데이트
            updateDailyScores();
            System.out.println("일일 점수 업데이트 완료");

            // 5. 무작위 피드(게시글, 댓글, 좋아요) 생성
            createRandomFeeds(memberIds);
            System.out.println("무작위 피드 생성 완료");

            System.out.println("무작위 데이터 생성 완료!");
        } catch (Exception e) {
            // 최상위 레벨에서 예외를 다시 던져서 트랜잭션이 롤백되는 것을 방지
            System.err.println("데이터 생성 중 치명적 오류 발생: " + e.getMessage());
            e.printStackTrace();
            // 여기서 예외를 다시 던지지 않음
            // throw e; -> 이 부분을 제거하거나 주석 처리
        }
    }

    // ===== 회원 관련 메서드 =====
    /**
     * 무작위 회원을 생성하는 메서드
     * @param count 생성할 회원 수
     * @return 생성된 회원 ID 목록
     */
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

    /**
     * 무작위 회원 정보를 생성하는 메서드
     * @param index 회원 인덱스 (중복 방지용)
     * @return 회원 등록 요청 객체
     */
    private MemberRegistRequest createRandomMemberRequest(int index) {
        MemberRegistRequest request = new MemberRegistRequest();

        // 고유한 계정명 생성
        request.setAccount("user" + index);
        request.setPassword("password" + index);

        // 고유한 이름 생성 (인덱스 포함)
        request.setUserName(generateRandomName(index));

        // 랜덤 신체 정보
        request.setHeight(ThreadLocalRandom.current().nextDouble(150.0, 190.0));
        request.setWeight(ThreadLocalRandom.current().nextDouble(45.0, 100.0));
        request.setGender(random.nextBoolean() ? kb.health.domain.Gender.MALE : kb.health.domain.Gender.FEMALE);
        request.setAge(ThreadLocalRandom.current().nextInt(18, 65));

        return request;
    }

    /**
     * 무작위 이름을 생성하는 메서드
     * @param index 이름 인덱스 (중복 방지용)
     * @return 생성된 이름
     */
    private String generateRandomName(int index) {
        // 랜덤 이름 생성 (인덱스를 포함하여 고유성 보장)
        String[] firstNames = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임", "한", "오", "서", "신", "권", "황", "안", "송", "류", "홍"};
        String[] lastNames = {"민준", "서준", "도윤", "예준", "시우", "하준", "지호", "준서", "준우", "민서",
                "지민", "지우", "지현", "예원", "하은", "다은", "서연", "서현", "민지", "수빈"};

        String baseName = firstNames[random.nextInt(firstNames.length)] + lastNames[random.nextInt(lastNames.length)];
        return baseName + "_" + index; // 인덱스를 추가하여 중복 방지
    }

    /**
     * 무작위 전화번호를 생성하는 메서드
     * @return 생성된 전화번호
     */
    private String generateRandomPhoneNumber() {
        // 랜덤 전화번호 생성 (010-XXXX-XXXX 형식)
        return String.format("010-%04d-%04d",
                ThreadLocalRandom.current().nextInt(1000, 10000),
                ThreadLocalRandom.current().nextInt(1000, 10000));
    }

    // ===== 팔로우 관련 메서드 =====
    /**
     * 무작위 팔로우 관계를 생성하는 메서드
     * @param memberIds 회원 ID 목록
     */
    private void createRandomFollowRelationships(List<Long> memberIds) {
        // 각 회원마다 무작위로 다른 회원을 팔로우
        for (Long memberId : memberIds) {
            // 이 회원이 팔로우할 다른 회원들을 무작위로 선택
            int followCount = ThreadLocalRandom.current().nextInt(0, FOLLOW_CONNECTIONS_PER_MEMBER + 1);

            // 팔로우할 회원 ID 목록 생성 (자기 자신 제외)
            List<Long> potentialFollowees = memberIds.stream()
                    .filter(id -> !id.equals(memberId))
                    .collect(Collectors.toList());

            // 무작위로 섞기
            Collections.shuffle(potentialFollowees);

            // 팔로우 관계 생성
            for (int i = 0; i < followCount && i < potentialFollowees.size(); i++) {
                memberService.follow(memberId, potentialFollowees.get(i));
            }
        }
    }

    // ===== 기록 관련 메서드 =====
    /**
     * ScoreTest 패턴을 활용한 간소화된 무작위 식단/운동 기록 생성 메서드
     * @param memberIds 회원 ID 목록
     */
    private void createSimplifiedRandomRecords(List<Long> memberIds) {
        LocalDate today = LocalDate.now();
        int totalDietRecords = 0;
        int totalExerciseRecords = 0;

        // 각 회원마다 특정 날짜들에 대한 기록 생성
        for (Long memberId : memberIds) {
            // 회원당 처리할 날짜 수를 제한하여 부하 감소 (DAYS_OF_HISTORY일 모두가 아닌 일부만)
            int memberDays = Math.min(DAYS_OF_HISTORY, random.nextInt(10) + 5); // 5-14일 사이

            for (int daysAgo = 0; daysAgo < memberDays; daysAgo++) {
                LocalDate recordDate = today.minusDays(daysAgo);

                // 하루에 1-3개의 식단 기록 생성 (MealType별로 하나씩)
                for (MealType mealType : getRandomMealTypes()) {
                    // 식품 ID는 1-8 사이 랜덤 값 (ScoreTest 패턴과 유사하게)
                    Long foodId = (long) (random.nextInt(8) + 1);

                    try {
                        // 칼로리는 300-800 사이 랜덤 값
                        int calories = 300 + random.nextInt(500);

                        // ScoreTest와 동일한 메서드로 기록 저장
                        DietRecordRequest dietRequest = new DietRecordRequest(foodId, calories, null, mealType);
                        recordService.saveDietRecord(dietRequest, memberId, recordDate);
                        totalDietRecords++;

                        System.out.println("식단 기록 생성: 회원 ID " + memberId + ", 날짜 " + recordDate +
                                ", 식사 타입 " + mealType + ", 음식 ID " + foodId);
                    } catch (Exception e) {
                        System.err.println("식단 기록 생성 중 오류: " + e.getMessage());
                    }
                }

                // 30% 확률로 운동 기록 추가
                if (random.nextDouble() < 0.3) {
                    try {
                        // ScoreTest와 유사하게 운동 기록 생성
                        ExerciseType exerciseType = random.nextBoolean() ?
                                ExerciseType.CARDIO : ExerciseType.WEIGHT;

                        String exerciseName = "운동" + (random.nextInt(5) + 1);
                        int duration = 20 + random.nextInt(40); // 20-60분
                        int caloriesBurned = 200 + random.nextInt(400); // 200-600칼로리

                        ExerciseRecordRequest exRequest = new ExerciseRecordRequest(
                                exerciseName, duration, caloriesBurned, exerciseType, null);

                        Long recordId = recordService.saveExerciseRecord(exRequest, memberId, recordDate);
                        totalExerciseRecords++;

                        // 80% 확률로 운동 완료 표시
                        if (random.nextDouble() < 0.8) {
                            ExerciseRecord record = recordService.getExerciseRecord(recordId);
                            record.setExercised(true);
                        }

                        System.out.println("운동 기록 생성: 회원 ID " + memberId + ", 날짜 " + recordDate +
                                ", 운동 타입 " + exerciseType + ", 이름 " + exerciseName);
                    } catch (Exception e) {
                        System.err.println("운동 기록 생성 중 오류: " + e.getMessage());
                    }
                }
            }
        }

        System.out.println("총 " + totalDietRecords + "개의 식단 기록과 " + totalExerciseRecords + "개의 운동 기록이 생성되었습니다.");
    }

    /**
     * 무작위 식사 타입 목록 생성
     * 아침, 점심, 저녁 중 1-3개를 무작위로 선택
     */
    private List<MealType> getRandomMealTypes() {
        List<MealType> allTypes = Arrays.asList(MealType.values());
        Collections.shuffle(allTypes);

        // 1-3개의 식사 타입 선택
        int count = random.nextInt(3) + 1;
        return allTypes.subList(0, Math.min(count, allTypes.size()));
    }

    // ===== 점수 관련 메서드 =====
    /**
     * 모든 날짜에 대해 일일 점수를 업데이트하는 메서드
     */
    private void updateDailyScores() {
        LocalDate today = LocalDate.now();

        System.out.println("모든 날짜에 대해 일일 점수 업데이트 중...");
        // 모든 날짜에 대해 일일 점수 업데이트 (과거부터 현재까지)
        for (int daysAgo = DAYS_OF_HISTORY - 1; daysAgo >= 0; daysAgo--) {
            LocalDate scoreDate = today.minusDays(daysAgo);

            try {
                System.out.println("  - " + scoreDate + " 데이터 점수 계산 중...");
                scoreService.updateDailyScoresForAllMembers(scoreDate);
            } catch (Exception e) {
                System.err.println("점수 업데이트 중 오류 발생 (" + scoreDate + "): " + e.getMessage());
            }
        }

        System.out.println("일일 점수 업데이트 완료!");
    }

    // ===== 피드 관련 메서드 =====
    /**
     * 무작위 피드 데이터(게시글, 댓글, 좋아요)를 생성하는 메서드
     * @param memberIds 회원 ID 목록
     */
    private void createRandomFeeds(List<Long> memberIds) {
        System.out.println("무작위 피드 데이터 생성 중...");

        // 1. 각 회원별로 무작위 게시글 생성
        List<Long> postIds = createRandomPosts(memberIds);
        System.out.println(postIds.size() + "개의 게시글 생성 완료");

        if (postIds.isEmpty()) {
            System.out.println("생성된 게시글이 없어 댓글과 좋아요를 추가할 수 없습니다.");
            return;
        }

        // 2. 생성된 게시글에 무작위 댓글 추가
        createRandomComments(memberIds, postIds);

        // 3. 게시글에 무작위 좋아요 추가
        createRandomLikes(memberIds, postIds);

        System.out.println("피드 데이터 생성 완료!");
    }

    /**
     * 무작위 게시글을 생성하는 메서드
     * @param memberIds 회원 ID 목록
     * @return 생성된 게시글 ID 목록
     */
    private List<Long> createRandomPosts(List<Long> memberIds) {
        List<Long> createdPostIds = new ArrayList<>();

        // 각 회원별로 게시글 생성
        for (Long memberId : memberIds) {
            // 이 회원이 작성할 게시글 수 (MIN_POSTS_PER_MEMBER ~ MAX_POSTS_PER_MEMBER)
            int postCount = ThreadLocalRandom.current().nextInt(MIN_POSTS_PER_MEMBER, MAX_POSTS_PER_MEMBER + 1);

            for (int i = 0; i < postCount; i++) {
                try {
                    // 무작위 제목과 내용 생성
                    String title = generateRandomPostTitle();
                    String content = generateRandomPostContent();

                    // 이미지 URL (80%의 확률로 null, 20%의 확률로 가상 URL)
                    String imageUrl = random.nextDouble() < 0.8 ? null :
                            "https://example.com/images/health_" + random.nextInt(1000) + ".jpg";

                    // 게시글 생성 요청 객체
                    PostCreateRequest request = new PostCreateRequest();
                    request.setTitle(title);
                    request.setContent(content);

                    // 게시글 저장
                    feedService.savePost(memberId, request, imageUrl);
                } catch (Exception e) {
                    System.err.println("게시글 생성 중 오류 발생: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        // 저장된 모든 게시글의 ID 조회
        // PostRepository를 사용하여 모든 게시글 조회
        List<Post> allPosts = postRepository.findAll();
        createdPostIds = allPosts.stream().map(Post::getId).collect(Collectors.toList());

        return createdPostIds;
    }

    /**
     * 게시글에 무작위 댓글을 추가하는 메서드
     * @param memberIds 회원 ID 목록
     * @param postIds 게시글 ID 목록
     */
    private void createRandomComments(List<Long> memberIds, List<Long> postIds) {
        // 각 게시글에 무작위 댓글 추가
        for (Long postId : postIds) {
            // 이 게시글에 달릴 댓글 수 (MIN_COMMENTS_PER_POST ~ MAX_COMMENTS_PER_POST)
            int commentCount = ThreadLocalRandom.current().nextInt(MIN_COMMENTS_PER_POST, MAX_COMMENTS_PER_POST + 1);

            for (int i = 0; i < commentCount; i++) {
                try {
                    // 무작위로 댓글 작성자 선택
                    Long commenterId = memberIds.get(random.nextInt(memberIds.size()));

                    // 무작위 댓글 내용 생성
                    String commentText = generateRandomCommentText();

                    // 댓글 생성 요청 객체
                    CommentCreateRequest request = new CommentCreateRequest();
                    request.setComment(commentText);

                    // 댓글 저장
                    feedService.saveComment(commenterId, postId, request);
                } catch (Exception e) {
                    System.err.println("댓글 생성 중 오류 발생 (게시글 ID: " + postId + "): " + e.getMessage());
                }
            }
        }

        System.out.println("댓글 생성 완료");
    }

    /**
     * 게시글에 무작위 좋아요를 추가하는 메서드
     * @param memberIds 회원 ID 목록
     * @param postIds 게시글 ID 목록
     */
    private void createRandomLikes(List<Long> memberIds, List<Long> postIds) {
        int totalLikesAdded = 0;

        // 각 회원이 무작위로 게시글에 좋아요 추가
        for (Long memberId : memberIds) {
            // 모든 게시글을 살펴보면서 LIKE_PROBABILITY 확률로 좋아요 추가
            for (Long postId : postIds) {
                if (random.nextDouble() < LIKE_PROBABILITY) {
                    try {
                        // 좋아요 토글
                        boolean isLiked = feedService.postLikeToggle(memberId, postId);
                        if (isLiked) {
                            totalLikesAdded++;
                        }
                    } catch (Exception e) {
                        System.err.println("좋아요 추가 중 오류 발생 (게시글 ID: " + postId + "): " + e.getMessage());
                    }
                }
            }
        }

        System.out.println("좋아요 " + totalLikesAdded + "개 추가 완료");
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
}