package kb.health.domain;

import jakarta.persistence.*;
import kb.health.domain.record.DietRecord;
import kb.health.domain.record.ExerciseRecord;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_account", unique = true, nullable = false)
    private String account;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(name = "member_score")
    private int score;

    //닉네임
    @Column(name = "user_name", unique = true, nullable = false)
    private String userName;

    //기본 이미지 설정 경로 필요
    @Column(name = "profile_image_url")
    private String profileImageUrl = "/images/default_profile.png";

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


    /* 빌더 */
    public static Member create(String account, String userName, String password, String phoneNumber) {
        Member member = new Member();
        member.setAccount(account);
        member.setUserName(userName);
        member.setPassword(password);
        member.setPhoneNumber(phoneNumber);
        return member;
    }
}
