package kb.health.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//static으로 새 멤버 반환하는거 만들기
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    private String password;

    //닉네임
    private String userName;

    /**
     * cascade
     * 1. 회원(Member)이 삭제되면, 그 사람이 했던 모든 팔로우(Follow) 정보도 같이 삭제돼야 함
     * 2.
     */
    @OneToMany(mappedBy = "from", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followings = new ArrayList<>();

    @OneToMany(mappedBy = "to", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers = new ArrayList<>();
}
