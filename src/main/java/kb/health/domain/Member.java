package kb.health.domain;

import jakarta.persistence.*;
import kb.health.controller.request.MemberBodyInfoEditRequest;
import kb.health.controller.request.MemberEditRequest;
import kb.health.domain.chat.ChatMessage;
import kb.health.domain.feed.PostLike;
import kb.health.domain.notification.Notification;
import kb.health.domain.record.DietRecord;
import kb.health.domain.record.ExerciseRecord;
import kb.health.controller.request.MemberRegistRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_account", unique = true, nullable = false)
    private String account;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    //닉네임
    @Column(name = "user_name", unique = true, nullable = false)
    private String userName;

    //기본 이미지 설정 경로 필요
    @Column(name = "profile_image_url")
    private String profileImageUrl = "/images/default_profile.png";

    //일일 점수
    @Column(name = "total_score", scale = 2)
    private double totalScore;

    //기본 점수 (10일 기준)
    @Column(name = "base_score", scale = 2)
    private double baseScore;

    private int previousTotalRank;  // 이전 총점 기준 순위
    private int previousBaseRank;   // 이전 최근 10일 점수 기준 순위
//    private LocalDate rankUpdatedAt; // 랭킹 업데이트 일자
    private boolean isNewMember = true; // 신규 회원 여부 (기본값: true)

    //신체 정보
    @Embedded
    private BodyInfo bodyInfo;

    //필요 영양소
    @Embedded
    private DailyNutritionStandard dailyNutritionStandard;

    /**
     * 알림
     */
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    /**
     * 채팅
     */
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> sentMessages = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> receivedMessages = new ArrayList<>();

    /**
     * FOLLOW
     * cascade
     * 1. 회원(Member)이 삭제되면, 그 사람이 했던 모든 팔로우(Follow) 정보도 같이 삭제해야 함
     */
    @OneToMany(mappedBy = "from", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followings = new ArrayList<>();

    @OneToMany(mappedBy = "to", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers = new ArrayList<>();

    /**
     * 식단, 운동 기록
     */
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietRecord> dietRecords = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseRecord> exerciseRecords = new ArrayList<>();

    /**
     * 일일 점수
     */
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyScore> dailyScores = new ArrayList<>();

    //좋아요 누른 게시글 확인 및 편의 메서드
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    public void addPostLike(PostLike postLike) {
        this.postLikes.add(postLike);
        postLike.setMember(this);
    }

    public void removePostLike(PostLike postLike) {
        this.postLikes.remove(postLike);
        postLike.setMember(null);
    }


    /* 빌더 */
    // 테스트용
    public static Member create(String account, String userName, String password, String phoneNumber) {
        Member member = new Member();
        member.setAccount(account);
        member.setUserName(userName);
        member.setPassword(password);
        member.setPhoneNumber(phoneNumber);

        return member;
    }

    public static Member create(MemberRegistRequest request) {
        Member member = new Member();
        member.setAccount(request.getAccount());
        member.setUserName(request.getUserName());
        member.setPassword(request.getPassword());
        member.setPhoneNumber(request.getPhoneNumber());

        // 신체 정보 세팅
        BodyInfo bodyInfo = new BodyInfo(
                request.getHeight(),
                request.getWeight(),
                request.getGender(),
                request.getAge()
        );
        member.setBodyInfo(bodyInfo);

        DailyNutritionStandard nutritionStandard = DailyNutritionStandard.calculate(bodyInfo);
        member.setDailyNutritionStandard(nutritionStandard);

        return member;
    }

    /**
     * 수정
     */
    //계정정보 수정
    public void updateAccountInfo(MemberEditRequest memberEditRequest) {
        this.password = memberEditRequest.getPassword();
        this.userName = memberEditRequest.getUserName();

        if(memberEditRequest.getProfileImageUrl() != null){
            this.profileImageUrl = memberEditRequest.getProfileImageUrl();
        } else {
            this.profileImageUrl = "/images/default_profile.png";
        }
    }

    //신체정보 수정
    public void updateBodyInfo(MemberBodyInfoEditRequest request) {
        this.bodyInfo.setHeight(request.getHeight());
        this.bodyInfo.setWeight(request.getWeight());
        this.bodyInfo.setGender(request.getGender());
        this.bodyInfo.setAge(request.getAge());

        // 새로운 신체 정보에 따른 영양소 재계산
        this.dailyNutritionStandard = DailyNutritionStandard.calculate(this.bodyInfo);
    }

    /**
     * 랭킹 업데이트
     */
    // 랭킹 정보 업데이트 메서드
    public void updateRankInfo(int totalRank, int baseRank) {
        this.previousTotalRank = totalRank;
        this.previousBaseRank = baseRank;
        this.isNewMember = false; // 랭킹이 업데이트되면 더 이상 신규 회원이 아님
    }
}
