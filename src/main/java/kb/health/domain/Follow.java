package kb.health.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private Member follower; //자신
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private Member following; //팔로우 한 사람

    /* 연관관계 편의 메서드 */
    private void setFollower(Member follower) {
        if (this.follower != null) {
            this.follower.getFollowings().remove(this);  // 기존 follower에서 제거
        }
        this.follower = follower;
        follower.getFollowings().add(this);  // 새로운 follower에 추가
    }

    private void setFollowing(Member following) {
        if (this.following != null) {
            this.following.getFollowers().remove(this);  // 기존 following에서 제거
        }
        this.following = following;
        following.getFollowers().add(this);  // 새로운 following에 추가
    }

    public static Follow createFollow(Member follower, Member following) {
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);

        return follow;
    }
}
